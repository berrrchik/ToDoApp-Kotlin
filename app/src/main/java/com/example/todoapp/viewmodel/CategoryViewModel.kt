package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.storage.CategoryStorage
import com.example.todoapp.storage.TaskStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryStorage = CategoryStorage(application)
    private val taskStorage = TaskStorage(application)
    
    private val _categories = MutableStateFlow<List<TaskCategory>>(emptyList())
    val categories: StateFlow<List<TaskCategory>> = _categories.asStateFlow()
    
    private val _userCategories = MutableStateFlow<List<TaskCategory>>(emptyList())
    val userCategories: StateFlow<List<TaskCategory>> = _userCategories.asStateFlow()
    
    private val _defaultCategories = MutableStateFlow<List<TaskCategory>>(emptyList())
    val defaultCategories: StateFlow<List<TaskCategory>> = _defaultCategories.asStateFlow()
    
    private val _hiddenDefaultCategories = MutableStateFlow<List<TaskCategory>>(emptyList())
    val hiddenDefaultCategories: StateFlow<List<TaskCategory>> = _hiddenDefaultCategories.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadCategories()
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allWithHidden = categoryStorage.getAllCategoriesWithHidden()
                _categories.value = categoryStorage.getAllCategories()
                _userCategories.value = categoryStorage.getUserCategories()
                
                // Фильтруем и сортируем стандартные категории
                val defaultCats = allWithHidden.filter { it.isDefault }
                _defaultCategories.value = defaultCats.filter { !it.isHidden }
                _hiddenDefaultCategories.value = defaultCats.filter { it.isHidden }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при загрузке категорий: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addCategory(name: String) {
        if (name.isBlank()) {
            _errorMessage.value = "Название категории не может быть пустым"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newCategory = categoryStorage.createCategory(name)
                loadCategories() // Перезагружаем все категории
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteCategory(categoryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (categoryStorage.deleteCategory(categoryId)) {
                    // Обновляем задачи, у которых была удаленная категория
                    categoryStorage.updateTasksAfterCategoryDeletion(categoryId, taskStorage)
                    loadCategories() // Перезагружаем все категории
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Не удалось удалить категорию"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun hideDefaultCategory(categoryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (categoryStorage.hideDefaultCategory(categoryId)) {
                    // Обновляем задачи, у которых была скрытая категория
                    categoryStorage.updateTasksAfterCategoryHidden(categoryId, taskStorage)
                    loadCategories() // Перезагружаем все категории
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Не удалось скрыть категорию"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun showDefaultCategory(categoryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (categoryStorage.showDefaultCategory(categoryId)) {
                    loadCategories() // Перезагружаем все категории
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Не удалось отобразить категорию"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
} 