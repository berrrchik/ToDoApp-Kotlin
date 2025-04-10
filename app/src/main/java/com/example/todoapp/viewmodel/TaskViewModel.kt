// taskviewmodel с апи заглушкой
package com.example.todoapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.todoapp.api.TaskApiService
import com.example.todoapp.api.MockTaskApiService
import com.example.todoapp.model.Task
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        viewModelScope.launch {
            _isDarkTheme.value = !_isDarkTheme.value
        }
    }

    private val api: TaskApiService = MockTaskApiService()

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
    
    private var searchJob: Job? = null
    
    private val PREF_SEARCH_HISTORY = "search_history"
    private val MAX_HISTORY_ITEMS = 10

    init {
        loadTasks()
        loadSearchHistory()
    }
    
    private fun loadSearchHistory() {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("todo_app_prefs", Context.MODE_PRIVATE)
        val historySet = sharedPreferences.getStringSet(PREF_SEARCH_HISTORY, HashSet()) ?: HashSet()
        
        // Преобразуем Set в список и сортируем по времени добавления (если доступно)
        // Так как стандартные SharedPreferences не сохраняют порядок, нам нужно использовать
        // дополнительный механизм для сортировки (можно сохранять timestampы вместе с запросами)
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
                _tasks.value = api.getTasks()
            } catch (e: Exception) {
                // Обработка ошибок
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
                val newTask = api.createTask(task)
                _tasks.value = _tasks.value + newTask
            } catch (e: Exception) {
                // Обработка ошибок
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedTask = api.updateTask(task)
                _tasks.value = _tasks.value.map {
                    if (it.id == task.id) updatedTask else it
                }
            } catch (e: Exception) {
                // Обработка ошибок
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (api.deleteTask(taskId)) {
                    _tasks.value = _tasks.value.filterNot { it.id == taskId }
                }
            } catch (e: Exception) {
                // Обработка ошибок
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// taskviewmodel без апи заглушки

//package com.example.todoapp.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.todoapp.model.Task
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class TaskViewModel : ViewModel() {
//    private val _searchQuery = MutableStateFlow("")
//    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
//
//    private val _tasks = MutableStateFlow<List<Task>>(
//        List(3) { index ->
//            Task(
//                id = index,
//                title = "Задача ${index + 1}",
//                description = "Описание задачи ${index + 1}",
//                isCompleted = false,
//                deadline = null
//            )
//        }
//    )
//    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
//
//    fun updateSearchQuery(query: String) {
//        viewModelScope.launch {
//            _searchQuery.value = query
//        }
//    }
//
//    fun updateTasks(newTasks: List<Task>) {
//        viewModelScope.launch {
//            _tasks.value = newTasks
//        }
//    }
//}
