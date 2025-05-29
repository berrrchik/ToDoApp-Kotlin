package com.example.todoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.repository.CategoryRepository
import com.example.todoapp.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "CategoryViewModel"
    private val categoryRepository = CategoryRepository(application)
    private val taskRepository = TaskRepository(application)
    
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
                Log.d(TAG, "loadCategories: загрузка категорий")
                val allWithHidden = categoryRepository.getAllCategoriesWithHidden()
                val allCategories = categoryRepository.getAllCategories()
                
                // Удаляем дубликаты категорий
                val uniqueCategories = allCategories.distinctBy { it.id }
                _categories.value = uniqueCategories
                
                // Получаем пользовательские категории, исключая стандартные по флагу isDefault
                val userCats = allCategories.filter { !it.isDefault }
                _userCategories.value = userCats
                
                // Фильтруем и сортируем стандартные категории
                val defaultCats = allWithHidden
                    .filter { it.isDefault }
                    .distinctBy { it.id }
                
                _defaultCategories.value = defaultCats.filter { !it.isHidden }
                _hiddenDefaultCategories.value = defaultCats.filter { it.isHidden }
                
                // Если стандартных категорий нет, пробуем их создать
                if (_defaultCategories.value.isEmpty() && _hiddenDefaultCategories.value.isEmpty()) {
                    Log.w(TAG, "loadCategories: стандартные категории отсутствуют, запускаем инициализацию")
                    val defaultCategories = categoryRepository.initializeDefaultCategories()
                    _defaultCategories.value = defaultCategories.filter { !it.isHidden }
                    _userCategories.value = userCats // Обновляем пользовательские категории
                    _categories.value = defaultCategories + userCats // Обновляем общий список
                }
                
                Log.d(TAG, "loadCategories: загружено ${_categories.value.size} категорий")
                Log.d(TAG, "loadCategories: стандартных категорий: ${_defaultCategories.value.size}")
                Log.d(TAG, "loadCategories: скрытых стандартных категорий: ${_hiddenDefaultCategories.value.size}")
                Log.d(TAG, "loadCategories: пользовательских категорий: ${_userCategories.value.size}")
            } catch (e: Exception) {
                Log.e(TAG, "loadCategories: ошибка при загрузке категорий", e)
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
        
        // Проверяем, не совпадает ли имя со стандартными категориями
        if (TaskCategory.DEFAULT_CATEGORY_NAMES.any { it.equals(name, ignoreCase = true) }) {
            _errorMessage.value = "Категория с таким названием уже существует в стандартных категориях"
            return
        }
        
        // Проверяем, не совпадает ли имя с существующими пользовательскими категориями
        if (_userCategories.value.any { it.name.equals(name, ignoreCase = true) }) {
            _errorMessage.value = "Категория с таким названием уже существует"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "addCategory: создание категории '$name'")
                val newCategory = categoryRepository.createCategory(name)
                if (newCategory != null) {
                    Log.d(TAG, "addCategory: категория успешно создана")
                    loadCategories() // Перезагружаем все категории
                    _errorMessage.value = null
                } else {
                    Log.e(TAG, "addCategory: не удалось создать категорию")
                    _errorMessage.value = "Не удалось создать категорию"
                }
            } catch (e: Exception) {
                Log.e(TAG, "addCategory: ошибка при создании категории", e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Проверяем, что категория не стандартная
                val category = _categories.value.find { it.id == categoryId }
                if (category?.isDefault == true) {
                    Log.e(TAG, "deleteCategory: попытка удалить стандартную категорию ${category.name}")
                    _errorMessage.value = "Нельзя удалить стандартную категорию"
                    _isLoading.value = false
                    return@launch
                }
                
                Log.d(TAG, "deleteCategory: удаление категории с ID '$categoryId'")
                val result = categoryRepository.deleteCategory(categoryId)
                
                // Всегда обновляем список категорий после попытки удаления,
                // даже если получен ответ с ошибкой, т.к. сервер мог удалить категорию
                Log.d(TAG, "deleteCategory: обновляем список категорий, результат удаления: $result")
                loadCategories()
                
                if (!result) {
                    // Отображаем сообщение об ошибке, но не блокируем работу
                    Log.e(TAG, "deleteCategory: не удалось удалить категорию")
//                    _errorMessage.value = "Не удалось удалить категорию"
                } else {
                    // Если удаление прошло успешно по ответу сервера
                    Log.d(TAG, "deleteCategory: категория успешно удалена")
                    _errorMessage.value = null
                    
                    // Обновляем задачи, связанные с удаленной категорией
                    val tasks = taskRepository.getAllTasks()
                    tasks.filter { it.category.id == categoryId }.forEach { task ->
                        taskRepository.updateTask(task.copy(category = TaskCategory.getDefaultCategory()))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "deleteCategory: исключение при удалении категории", e)
                _errorMessage.value = e.message
                
                // Даже при исключении обновляем список категорий
                loadCategories()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun hideDefaultCategory(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "hideDefaultCategory: скрытие категории с ID '$categoryId'")
                if (categoryRepository.hideDefaultCategory(categoryId)) {
                    // Загружаем задачи, чтобы обновить те, что использовали скрытую категорию
                    val tasks = taskRepository.getAllTasks()
                    tasks.filter { it.category.id == categoryId }.forEach { task ->
                        taskRepository.updateTask(task.copy(category = TaskCategory.getDefaultCategory()))
                    }
                    Log.d(TAG, "hideDefaultCategory: категория успешно скрыта")
                    loadCategories() // Перезагружаем все категории
                    _errorMessage.value = null
                } else {
                    Log.e(TAG, "hideDefaultCategory: не удалось скрыть категорию")
                    _errorMessage.value = "Не удалось скрыть категорию"
                }
            } catch (e: Exception) {
                Log.e(TAG, "hideDefaultCategory: ошибка при скрытии категории", e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun showDefaultCategory(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "showDefaultCategory: отображение категории с ID '$categoryId'")
                if (categoryRepository.showDefaultCategory(categoryId)) {
                    Log.d(TAG, "showDefaultCategory: категория успешно отображена")
                    loadCategories() // Перезагружаем все категории
                    _errorMessage.value = null
                } else {
                    Log.e(TAG, "showDefaultCategory: не удалось отобразить категорию")
                    _errorMessage.value = "Не удалось отобразить категорию"
                }
            } catch (e: Exception) {
                Log.e(TAG, "showDefaultCategory: ошибка при отображении категории", e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    // Метод для очистки кэша категорий
    fun clearCategoryCache() {
        Log.d(TAG, "clearCategoryCache: очистка кэша категорий")
        categoryRepository.clearCache()
        // Очищаем все списки категорий
        _categories.value = emptyList()
        _userCategories.value = emptyList()
        _defaultCategories.value = emptyList()
        _hiddenDefaultCategories.value = emptyList()
    }
    
    // Метод для принудительного создания стандартных категорий
    fun createDefaultCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "createDefaultCategories: запуск принудительного создания стандартных категорий")
                val defaultCategories = categoryRepository.initializeDefaultCategories()
                loadCategories() // Перезагружаем все категории после создания стандартных
            } catch (e: Exception) {
                Log.e(TAG, "createDefaultCategories: ошибка при создании стандартных категорий", e)
                _errorMessage.value = "Ошибка при создании стандартных категорий: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Метод для обновления статуса стандартных категорий в базе данных
    fun updateDefaultCategoriesStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "updateDefaultCategoriesStatus: запуск обновления статусов стандартных категорий")
                val success = categoryRepository.updateDefaultCategoriesStatus()
                
                if (success) {
                    Log.d(TAG, "updateDefaultCategoriesStatus: статусы успешно обновлены")
                    _errorMessage.value = null
                    // Перезагружаем категории, чтобы обновить UI
                    loadCategories()
                } else {
                    Log.e(TAG, "updateDefaultCategoriesStatus: не удалось обновить статусы")
                    _errorMessage.value = "Не удалось обновить статусы стандартных категорий"
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateDefaultCategoriesStatus: ошибка при обновлении статусов", e)
                _errorMessage.value = "Ошибка при обновлении статусов: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Метод для получения только видимых категорий (без скрытых)
    fun getVisibleCategories(): List<TaskCategory> {
        // Фильтруем категории, исключая скрытые
        return categories.value.filterNot { it.isHidden }
    }
} 