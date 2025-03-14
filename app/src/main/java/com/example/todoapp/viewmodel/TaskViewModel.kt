// taskviewmodel с апи заглушкой
package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.api.TaskApiService
import com.example.todoapp.api.MockTaskApiService
import com.example.todoapp.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        viewModelScope.launch {
            _isDarkTheme.value = !_isDarkTheme.value
        }
    }

    private val api: TaskApiService = MockTaskApiService()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
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
        viewModelScope.launch {
            _searchQuery.value = query
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
