package com.example.todoapp.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.todoapp.model.Task
import com.example.todoapp.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TaskViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val TAG = "TaskViewModel"
    
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Репозиторий задач
    private val taskRepository = TaskRepository(application)

    private val _searchQuery = MutableStateFlow(savedStateHandle.get<String>("searchQuery") ?: "")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _isHistoryVisible = MutableStateFlow(false)
    val isHistoryVisible: StateFlow<Boolean> = _isHistoryVisible.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private var searchJob: Job? = null
    
    private val PREF_SEARCH_HISTORY = "search_history"
    private val MAX_HISTORY_ITEMS = 10

    // Событие для оповещения о успешном удалении задачи
    private val _taskDeletedEvent = MutableSharedFlow<String>()
    val taskDeletedEvent: SharedFlow<String> = _taskDeletedEvent.asSharedFlow()

    init {
        loadTasks()
        loadSearchHistory()
    }
    
    fun toggleTheme() {
        viewModelScope.launch {
            _isDarkTheme.value = !_isDarkTheme.value
        }
    }
    
    private fun loadSearchHistory() {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("todo_app_prefs", Context.MODE_PRIVATE)
        val historySet = sharedPreferences.getStringSet(PREF_SEARCH_HISTORY, HashSet()) ?: HashSet()
        
        // Преобразуем Set в список и сортируем по времени добавления (если доступно)
        _searchHistory.value = historySet.toList().take(MAX_HISTORY_ITEMS)
    }
    
    private fun saveSearchHistory(newHistory: List<String>) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("todo_app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            // Сначала очистим старую историю
            remove(PREF_SEARCH_HISTORY)
            // Затем сохраним новую
            putStringSet(PREF_SEARCH_HISTORY, newHistory.toSet())
            apply()
        }
    }

    fun addToSearchHistory(query: String) {
        if (query.isBlank()) return
        
        // Получаем текущую историю
        val currentHistory = _searchHistory.value.toMutableList()
        
        // Если запрос уже есть в истории, удаляем его (чтобы добавить заново в начало)
        currentHistory.remove(query)
        
        // Добавляем новый запрос в начало списка
        currentHistory.add(0, query)
        
        // Ограничиваем размер истории
        val newHistory = currentHistory.take(MAX_HISTORY_ITEMS)
        
        // Обновляем состояние и сохраняем
        _searchHistory.value = newHistory
        saveSearchHistory(newHistory)
    }
    
    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
        
        // Очищаем историю в SharedPreferences
        val sharedPreferences = getApplication<Application>().getSharedPreferences("todo_app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(PREF_SEARCH_HISTORY).apply()
    }
    
    fun setHistoryVisible(visible: Boolean) {
        _isHistoryVisible.value = visible
    }

    fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "loadTasks: начало загрузки задач")
                val loadedTasks = taskRepository.getAllTasks()
                Log.d(TAG, "loadTasks: получено ${loadedTasks.size} задач")
                _tasks.value = loadedTasks
                
                // Логируем идентификаторы задач для отладки
                if (loadedTasks.isNotEmpty()) {
                    Log.d(TAG, "loadTasks: идентификаторы задач: ${loadedTasks.map { it.id }}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadTasks: ошибка при загрузке задач", e)
                _errorMessage.value = "Ошибка при загрузке задач: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        savedStateHandle["searchQuery"] = query // Сохраняем в SavedStateHandle
        
        // Отмена предыдущего задания поиска, если оно существует
        searchJob?.cancel()
        
        // Создаём новое задание с задержкой
        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(2000) // 2 секунды задержки
            // Если пользователь добавил запрос в поиск, добавляем его в историю
            if (query.isNotEmpty()) {
                addToSearchHistory(query)
            }
            _isSearching.value = false
        }
    }

    fun createTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "createTask: создание задачи $task")
                val newTask = taskRepository.createTask(task)
                if (newTask != null) {
                    Log.d(TAG, "createTask: задача успешно создана $newTask")
                    val updatedTasks = _tasks.value.toMutableList()
                    updatedTasks.add(newTask)
                    _tasks.value = updatedTasks
                } else {
                    Log.e(TAG, "createTask: не удалось создать задачу")
                    _errorMessage.value = "Не удалось создать задачу"
                }
            } catch (e: Exception) {
                Log.e(TAG, "createTask: ошибка при создании задачи", e)
                _errorMessage.value = "Ошибка при создании задачи: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "updateTask: обновление задачи ${task.id}")
                val updatedTask = taskRepository.updateTask(task)
                if (updatedTask != null) {
                    Log.d(TAG, "updateTask: задача успешно обновлена $updatedTask")
                    _tasks.value = _tasks.value.map {
                        if (it.id == task.id) updatedTask else it
                    }
                } else {
                    Log.e(TAG, "updateTask: не удалось обновить задачу")
                    _errorMessage.value = "Не удалось обновить задачу"
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateTask: ошибка при обновлении задачи", e)
                _errorMessage.value = "Ошибка при обновлении задачи: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "deleteTask: удаление задачи $taskId")
                if (taskRepository.deleteTask(taskId)) {
                    Log.d(TAG, "deleteTask: задача успешно удалена $taskId")
                    _tasks.value = _tasks.value.filterNot { it.id == taskId }
                } else {
                    Log.e(TAG, "deleteTask: не удалось удалить задачу")
                    _errorMessage.value = "Не удалось удалить задачу"
                }
            } catch (e: Exception) {
                Log.e(TAG, "deleteTask: ошибка при удалении задачи", e)
                _errorMessage.value = "Ошибка при удалении задачи: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun permanentlyDeleteTask(taskId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "permanentlyDeleteTask: полное удаление задачи $taskId")
                if (taskRepository.permanentlyDeleteTask(taskId)) {
                    // Немедленно удаляем задачу из локального списка
                    _tasks.value = _tasks.value.filterNot { it.id == taskId }
                    
                    // Отправляем событие об успешном удалении
                    _taskDeletedEvent.emit(taskId)
                    
                    Log.d(TAG, "permanentlyDeleteTask: задача успешно удалена из базы данных $taskId")
                } else {
                    Log.e(TAG, "permanentlyDeleteTask: не удалось полностью удалить задачу")
                    _errorMessage.value = "Не удалось полностью удалить задачу"
                }
            } catch (e: Exception) {
                Log.e(TAG, "permanentlyDeleteTask: ошибка при полном удалении задачи", e)
                _errorMessage.value = "Ошибка при полном удалении задачи: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}