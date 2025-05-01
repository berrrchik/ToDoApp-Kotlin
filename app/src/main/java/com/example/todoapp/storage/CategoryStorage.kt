package com.example.todoapp.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.todoapp.model.TaskCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CategoryStorage(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("todo_app_categories", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    // Ключ для хранения списка категорий
    private val CATEGORIES_KEY = "categories_list"
    
    // Ключ для хранения последнего ID
    private val LAST_ID_KEY = "last_category_id"
    
    // Ключ для хранения скрытых стандартных категорий
    private val HIDDEN_DEFAULT_CATEGORIES_KEY = "hidden_default_categories"
    
    // Получить все категории (включая стандартные), но исключая скрытые
    fun getAllCategories(): List<TaskCategory> {
        val userCategories = getUserCategories()
        val hiddenCategoryIds = getHiddenDefaultCategoryIds()
        
        // Объединяем стандартные (не скрытые) и пользовательские категории
        val defaultCategories = TaskCategory.DEFAULT_CATEGORIES.map { category ->
            // Копируем категорию с установленным флагом isHidden, если она скрыта
            category.copy(isHidden = hiddenCategoryIds.contains(category.id))
        }
        
        // Возвращаем только видимые категории
        return defaultCategories.filter { !it.isHidden } + userCategories
    }
    
    // Получить все категории, включая скрытые
    fun getAllCategoriesWithHidden(): List<TaskCategory> {
        val userCategories = getUserCategories()
        val hiddenCategoryIds = getHiddenDefaultCategoryIds()
        
        // Объединяем стандартные и пользовательские категории
        val defaultCategories = TaskCategory.DEFAULT_CATEGORIES.map { category ->
            // Копируем категорию с установленным флагом isHidden, если она скрыта
            category.copy(isHidden = hiddenCategoryIds.contains(category.id))
        }
        
        return defaultCategories + userCategories
    }
    
    // Получить только пользовательские категории
    fun getUserCategories(): List<TaskCategory> {
        val categoriesJson = sharedPreferences.getString(CATEGORIES_KEY, "[]")
        val type = object : TypeToken<List<TaskCategory>>() {}.type
        return gson.fromJson(categoriesJson, type) ?: emptyList()
    }
    
    // Сохранить список пользовательских категорий
    private fun saveCategories(categories: List<TaskCategory>) {
        val categoriesJson = gson.toJson(categories)
        sharedPreferences.edit().putString(CATEGORIES_KEY, categoriesJson).apply()
    }
    
    // Получить следующий ID для новой категории
    private fun getNextId(): Int {
        val lastId = sharedPreferences.getInt(LAST_ID_KEY, TaskCategory.DEFAULT_CATEGORIES.size)
        val nextId = lastId + 1
        sharedPreferences.edit().putInt(LAST_ID_KEY, nextId).apply()
        return nextId
    }
    
    // Создать новую категорию
    fun createCategory(name: String): TaskCategory {
        val categories = getUserCategories().toMutableList()
        
        // Проверка, чтобы не было дубликатов
        if (categories.any { it.name.equals(name, ignoreCase = true) }) {
            throw IllegalArgumentException("Категория с таким именем уже существует")
        }
        
        // Проверка, чтобы не дублировать стандартные категории
        if (TaskCategory.DEFAULT_CATEGORIES.any { it.name.equals(name, ignoreCase = true) }) {
            throw IllegalArgumentException("Эта категория уже существует как стандартная")
        }
        
        val newCategory = TaskCategory(id = getNextId(), name = name)
        categories.add(newCategory)
        saveCategories(categories)
        return newCategory
    }
    
    // Удалить категорию по ID
    fun deleteCategory(categoryId: Int): Boolean {
        // Проверяем, что это не стандартная категория
        if (TaskCategory.DEFAULT_CATEGORIES.any { it.id == categoryId }) {
            throw IllegalArgumentException("Нельзя удалить стандартную категорию")
        }
        
        val categories = getUserCategories().toMutableList()
        val removed = categories.removeIf { it.id == categoryId }
        if (removed) {
            saveCategories(categories)
        }
        return removed
    }
    
    // Получить ID скрытых стандартных категорий
    fun getHiddenDefaultCategoryIds(): Set<Int> {
        return sharedPreferences.getStringSet(HIDDEN_DEFAULT_CATEGORIES_KEY, emptySet())
            ?.map { it.toInt() }
            ?.toSet() ?: emptySet()
    }
    
    // Скрыть стандартную категорию
    fun hideDefaultCategory(categoryId: Int): Boolean {
        // Проверяем, что это стандартная категория
        if (!TaskCategory.DEFAULT_CATEGORIES.any { it.id == categoryId }) {
            return false
        }
        
        // Проверяем, что это не единственная видимая стандартная категория
        val hiddenIds = getHiddenDefaultCategoryIds()
        if (hiddenIds.size >= TaskCategory.DEFAULT_CATEGORIES.size - 1) {
            throw IllegalArgumentException("Должна остаться хотя бы одна стандартная категория")
        }
        
        // Добавляем ID к списку скрытых
        val newHiddenIds = hiddenIds.toMutableSet()
        newHiddenIds.add(categoryId)
        
        // Сохраняем новый список
        sharedPreferences.edit()
            .putStringSet(HIDDEN_DEFAULT_CATEGORIES_KEY, newHiddenIds.map { it.toString() }.toSet())
            .apply()
        
        return true
    }
    
    // Показать скрытую стандартную категорию
    fun showDefaultCategory(categoryId: Int): Boolean {
        // Проверяем, что это стандартная категория
        if (!TaskCategory.DEFAULT_CATEGORIES.any { it.id == categoryId }) {
            return false
        }
        
        // Удаляем ID из списка скрытых
        val hiddenIds = getHiddenDefaultCategoryIds().toMutableSet()
        val removed = hiddenIds.remove(categoryId)
        
        if (removed) {
            // Сохраняем новый список
            sharedPreferences.edit()
                .putStringSet(HIDDEN_DEFAULT_CATEGORIES_KEY, hiddenIds.map { it.toString() }.toSet())
                .apply()
        }
        
        return removed
    }
    
    // Обновить задачи, которые используют удаленную категорию
    fun updateTasksAfterCategoryDeletion(categoryId: Int, taskStorage: TaskStorage) {
        val defaultCategory = TaskCategory.getDefaultCategory()
        val tasks = taskStorage.getAllTasks()
        
        tasks.forEach { task ->
            if (task.category.id == categoryId) {
                taskStorage.updateTask(task.copy(category = defaultCategory))
            }
        }
    }
    
    // Обновить задачи, которые используют скрытую категорию
    fun updateTasksAfterCategoryHidden(categoryId: Int, taskStorage: TaskStorage) {
        val defaultCategory = TaskCategory.getDefaultCategory()
        val tasks = taskStorage.getAllTasks()
        
        tasks.forEach { task ->
            if (task.category.id == categoryId) {
                taskStorage.updateTask(task.copy(category = defaultCategory))
            }
        }
    }
    
    // Получить категорию по ID
    fun getCategoryById(categoryId: Int): TaskCategory? {
        // Проверяем скрытые категории
        val hiddenIds = getHiddenDefaultCategoryIds()
        
        // Сначала ищем среди стандартных категорий
        val defaultCategory = TaskCategory.DEFAULT_CATEGORIES.find { it.id == categoryId }
        if (defaultCategory != null) {
            return defaultCategory.copy(isHidden = hiddenIds.contains(categoryId))
        }
        
        // Затем среди пользовательских
        return getUserCategories().find { it.id == categoryId }
    }
} 