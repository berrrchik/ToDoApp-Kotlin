package com.example.todoapp.repository

import android.content.Context
import android.util.Log
import com.example.todoapp.api.ApiClient
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.model.category.CategoryCreateRequest
import com.example.todoapp.model.category.CategoryResponse
import com.example.todoapp.model.category.CategoryUpdateRequest
import com.example.todoapp.model.category.HideCategoryRequest
import com.example.todoapp.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository(private val context: Context) {
    private val TAG = "CategoryRepository"
    private val categoryApi = ApiClient.categoryApi
    private val tokenManager = TokenManager(context)
    
    // Кэш категорий, полученных с сервера
    private var cachedCategories: List<TaskCategory>? = null
    
    suspend fun getAllCategories(): List<TaskCategory> {
        val token = tokenManager.getToken()
        
        if (token == null) {
            Log.w(TAG, "getAllCategories: токен отсутствует, возвращаем локальные категории")
            return TaskCategory.DEFAULT_CATEGORIES
        }
        
        // Используем кэшированные категории, если они есть
        if (cachedCategories != null) {
            return cachedCategories!!
        }
        
        return try {
            Log.d(TAG, "getAllCategories: запрос на сервер")
            val response = categoryApi.getAllCategories("Bearer $token")
            
            if (response.isSuccessful && response.body() != null) {
                // Получаем категории с сервера
                val categories = response.body()!!.categories.map { it.toTaskCategory() }
                Log.d(TAG, "getAllCategories: получено ${categories.size} категорий с сервера")
                
                // Если категорий нет, инициализируем стандартные категории
                if (categories.isEmpty()) {
                    Log.d(TAG, "getAllCategories: категории отсутствуют, создаем стандартные")
                    return initializeDefaultCategories()
                }
                
                // Проверяем, присутствуют ли все стандартные категории
                val existingDefaultCategories = categories.filter { it.isDefault }
                if (existingDefaultCategories.size < TaskCategory.DEFAULT_CATEGORY_NAMES.size) {
                    Log.d(TAG, "getAllCategories: некоторые стандартные категории отсутствуют, создаем недостающие")
                    return initializeDefaultCategories()
                }
                
                // Сохраняем в кэш
                cachedCategories = categories
                categories
            } else {
                Log.e(TAG, "getAllCategories: ошибка ${response.code()}")
                TaskCategory.DEFAULT_CATEGORIES
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllCategories: исключение", e)
            TaskCategory.DEFAULT_CATEGORIES
        }
    }
    
    // Инициализировать стандартные категории на сервере
    suspend fun initializeDefaultCategories(): List<TaskCategory> {
        val token = tokenManager.getToken() ?: return TaskCategory.DEFAULT_CATEGORIES
        
        try {
            Log.d(TAG, "initializeDefaultCategories: проверка существующих категорий")
            
            // Сначала получаем уже существующие категории
            val existingCategories = try {
                val response = categoryApi.getAllCategories("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!.categories
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "initializeDefaultCategories: ошибка при получении существующих категорий", e)
                emptyList()
            }
            
            // Проверяем, какие стандартные категории уже существуют (сравниваем по именам)
            val existingNames = existingCategories.map { it.name }
            
            // Категории, которые нужно создать (те, которых еще нет в existingNames)
            val categoriesToCreate = TaskCategory.DEFAULT_CATEGORY_NAMES.filter { 
                !existingNames.contains(it) 
            }
            
            Log.d(TAG, "initializeDefaultCategories: найдено ${existingNames.size} существующих категорий")
            Log.d(TAG, "initializeDefaultCategories: необходимо создать ${categoriesToCreate.size} категорий")
            
            // Находим существующие стандартные категории
            val existingDefaultCategories = existingCategories
                .filter { category -> 
                    TaskCategory.DEFAULT_CATEGORY_NAMES.contains(category.name)
                }
                .map { it.toTaskCategory() }
            
            Log.d(TAG, "initializeDefaultCategories: найдено ${existingDefaultCategories.size} существующих стандартных категорий")
            
            // Создаем только те категории, которых еще нет
            val createdCategories = mutableListOf<TaskCategory>()
            createdCategories.addAll(existingDefaultCategories)
            
            if (categoriesToCreate.isNotEmpty()) {
                Log.d(TAG, "initializeDefaultCategories: создание недостающих стандартных категорий (${categoriesToCreate.size})")
                
                for (categoryName in categoriesToCreate) {
                    Log.d(TAG, "initializeDefaultCategories: создание категории '$categoryName'")
                    val request = CategoryCreateRequest(name = categoryName)
                    
                    try {
                        val response = categoryApi.createCategory("Bearer $token", request)
                        if (response.isSuccessful && response.body() != null) {
                            // Преобразуем в TaskCategory и добавляем в список
                            val category = response.body()!!.toTaskCategory()
                            Log.d(TAG, "initializeDefaultCategories: категория создана успешно: $category")
                            createdCategories.add(category)
                        } else {
                            Log.e(TAG, "initializeDefaultCategories: ошибка при создании категории '$categoryName': ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "initializeDefaultCategories: исключение при создании категории '$categoryName'", e)
                    }
                }
            }
            
            // Если не удалось получить стандартные категории с сервера, возвращаем локальные
            if (createdCategories.isEmpty()) {
                Log.w(TAG, "initializeDefaultCategories: не удалось получить или создать стандартные категории")
                return TaskCategory.DEFAULT_CATEGORIES
            }
            
            // Обновляем кэш и возвращаем созданные категории
            Log.d(TAG, "initializeDefaultCategories: получено ${createdCategories.size} стандартных категорий")
            
            // Обновляем статусы isDefault для всех стандартных категорий
            updateDefaultCategoriesStatus()
            
            // Обновляем кэш после получения категорий
            val allCategories = try {
                val response = categoryApi.getAllCategories("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!.categories.map { it.toTaskCategory() }
                } else {
                    createdCategories
                }
            } catch (e: Exception) {
                createdCategories
            }
            
            cachedCategories = allCategories
            return allCategories
        } catch (e: Exception) {
            Log.e(TAG, "initializeDefaultCategories: исключение", e)
        }
        
        // В случае ошибки возвращаем локальные категории
        return TaskCategory.DEFAULT_CATEGORIES
    }
    
    // Очистка кэша категорий
    fun clearCache() {
        cachedCategories = null
    }
    
    suspend fun getAllCategoriesWithHidden(): List<TaskCategory> {
        val visibleCategories = getAllCategories()
        val hiddenCategories = getHiddenCategories()
        
        return visibleCategories + hiddenCategories
    }
    
    suspend fun getUserCategories(): List<TaskCategory> {
        val allCategories = getAllCategories()
        Log.d(TAG, "getUserCategories: всего категорий ${allCategories.size}, из них стандартных: ${allCategories.count { it.isDefault }}")
        return allCategories.filter { !it.isDefault }
    }
    
    suspend fun getHiddenCategories(): List<TaskCategory> {
        val token = tokenManager.getToken() ?: return emptyList()
        
        return try {
            val response = categoryApi.getHiddenCategories("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.categories.map { 
                    // Проверяем, является ли категория стандартной по названию
                    val isDefaultByName = TaskCategory.DEFAULT_CATEGORY_NAMES.contains(it.name)
                    it.toTaskCategory(forceDefault = isDefaultByName)
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun createCategory(name: String): TaskCategory? {
        val token = tokenManager.getToken() ?: return null
        
        // Проверяем, не совпадает ли имя со стандартными категориями
        val isDefaultCategory = TaskCategory.DEFAULT_CATEGORY_NAMES.any { it.equals(name, ignoreCase = true) }
        if (isDefaultCategory) {
            Log.w(TAG, "createCategory: попытка создать категорию с именем стандартной категории '$name'")
            return null
        }
        
        return try {
            val request = CategoryCreateRequest(name = name)
            val response = categoryApi.createCategory("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                // Очищаем кэш, т.к. категории изменились
                clearCache()
                // Преобразуем ответ в TaskCategory
                response.body()!!.toTaskCategory()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getCategoryById(categoryId: String): TaskCategory? {
        val token = tokenManager.getToken()
        
        if (token == null) {
            // Проверяем, это ID из стандартных категорий
            val defaultCategory = TaskCategory.DEFAULT_CATEGORIES.find { 
                it.id == categoryId
            }
            return defaultCategory
        }
        
        // Проверяем кэш категорий сначала
        val fromCache = cachedCategories?.find { it.id == categoryId }
        if (fromCache != null) {
            return fromCache
        }
        
        return try {
            Log.d(TAG, "getCategoryById: запрос категории $categoryId")
            val response = categoryApi.getCategoryById("Bearer $token", categoryId)
            
            if (response.isSuccessful && response.body() != null) {
                // Проверяем, является ли категория стандартной по названию
                val category = response.body()!!
                val isDefaultByName = TaskCategory.DEFAULT_CATEGORY_NAMES.contains(category.name)
                category.toTaskCategory(forceDefault = isDefaultByName)
            } else {
                Log.e(TAG, "getCategoryById: ошибка ${response.code()}")
                // Если категория не найдена на сервере, возвращаем первую категорию из доступных
                getAllCategories().firstOrNull()
            }
        } catch (e: Exception) {
            Log.e(TAG, "getCategoryById: исключение", e)
            // В случае ошибки возвращаем первую категорию из доступных
            getAllCategories().firstOrNull()
        }
    }
    
    suspend fun updateCategory(categoryId: String, name: String): TaskCategory? {
        val token = tokenManager.getToken() ?: return null
        
        return try {
            val request = CategoryUpdateRequest(
                id = categoryId,
                name = name
            )
            val response = categoryApi.updateCategory("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                // Очищаем кэш, т.к. категории изменились
                clearCache()
                // Проверяем, является ли категория стандартной по названию
                val isDefaultByName = TaskCategory.DEFAULT_CATEGORY_NAMES.contains(name)
                response.body()!!.toTaskCategory(forceDefault = isDefaultByName)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun deleteCategory(categoryId: String): Boolean {
        val token = tokenManager.getToken() ?: return false
        
        try {
            // Сначала получаем информацию о категории, чтобы проверить, можно ли её удалить
            val category = getCategoryById(categoryId)
            
            // Проверяем, является ли категория стандартной
            if (category?.isDefault == true) {
                Log.e(TAG, "deleteCategory: нельзя удалить стандартную категорию ${category.name}")
                return false
            }
            
            Log.d(TAG, "deleteCategory: попытка удалить категорию с ID $categoryId через DELETE-запрос")
            
            // Сначала пробуем через DELETE-запрос
            var response = categoryApi.deleteCategoryByDelete("Bearer $token", categoryId)
            
            // Если не удалось, пробуем через POST-запрос
            if (!response.isSuccessful) {
                Log.d(TAG, "deleteCategory: DELETE-запрос не сработал, пробуем через POST")
                response = categoryApi.deleteCategoryByPost("Bearer $token", categoryId)
            }
            
            if (response.isSuccessful) {
                val baseResponse = response.body()
                if (baseResponse?.success == true) {
                    Log.d(TAG, "deleteCategory: категория успешно удалена, сообщение: ${baseResponse.message}")
                    // Очищаем кэш, т.к. категории изменились
                    clearCache()
                    return true
                } else {
                    Log.e(TAG, "deleteCategory: сервер вернул успешный статус, но операция не удалась: ${baseResponse?.message}")
                    return false
                }
            } else {
                val errorCode = response.code()
                val errorBody = try {
                    response.errorBody()?.string() ?: "Неизвестная ошибка"
                } catch (e: Exception) {
                    "Ошибка при чтении тела ответа: ${e.message}"
                }
                
                // Если код ошибки 500, возможно категория была удалена, но сервер не смог
                // корректно сформировать ответ. Очищаем кэш на всякий случай.
                if (errorCode == 500) {
                    Log.w(TAG, "deleteCategory: получен код 500, возможно категория была удалена: $errorBody")
                    clearCache()
                }
                
                Log.e(TAG, "deleteCategory: ошибка - код: $errorCode, тело: $errorBody")
                return false
            }
        } catch (e: Exception) {
            Log.e(TAG, "deleteCategory: исключение", e)
            // На всякий случай очищаем кэш даже при исключении
            clearCache()
            return false
        }
    }
    
    // Метод для скрытия категории через прямое обновление
    private suspend fun updateCategoryHiddenStatus(categoryId: String, isHidden: Boolean, categoryName: String): Boolean {
        val token = tokenManager.getToken() ?: return false
        
        return try {
            // Получаем информацию о категории
            val category = try {
                val response = categoryApi.getCategoryById("Bearer $token", categoryId)
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else {
                    Log.e(TAG, "updateCategoryHiddenStatus: не удалось получить информацию о категории $categoryId")
                    return false
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateCategoryHiddenStatus: исключение при получении категории", e)
                return false
            }
            
            // Для стандартных категорий мы не меняем название и isDefault
            val updateRequest = if (category.isDefault) {
                CategoryUpdateRequest(
                    id = categoryId,
                    isHidden = isHidden
                    // Не указываем name и isDefault для стандартных категорий
                )
            } else {
                CategoryUpdateRequest(
                    id = categoryId,
                    isHidden = isHidden,
                    name = categoryName
                )
            }
            
            Log.d(TAG, "updateCategoryHiddenStatus: запрос для скрытия/отображения категории: $updateRequest")
            
            val updateResponse = categoryApi.updateCategory("Bearer $token", updateRequest)
            
            if (updateResponse.isSuccessful) {
                Log.d(TAG, "updateCategoryHiddenStatus: статус категории успешно обновлен")
                // Очищаем кэш
                clearCache()
                true
            } else {
                val errorCode = updateResponse.code()
                val errorBody = try {
                    updateResponse.errorBody()?.string() ?: "Неизвестная ошибка"
                } catch (e: Exception) {
                    "Ошибка при чтении тела ответа: ${e.message}"
                }
                Log.e(TAG, "updateCategoryHiddenStatus: ошибка обновления - код: $errorCode, тело: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateCategoryHiddenStatus: исключение", e)
            false
        }
    }
    
    suspend fun hideDefaultCategory(categoryId: String): Boolean {
        val token = tokenManager.getToken() ?: return false
        
        try {
            // Проверяем, сколько видимых категорий осталось, чтобы не скрыть все
            val allCategories = getAllCategories()
            
            // Идентифицируем категорию для скрытия
            val categoryToHide = allCategories.find { it.id == categoryId }
            if (categoryToHide == null) {
                Log.e(TAG, "hideDefaultCategory: категория с ID $categoryId не найдена")
                return false
            }
            
            // Проверяем, что категория действительно стандартная
            if (!categoryToHide.isDefault) {
                Log.e(TAG, "hideDefaultCategory: попытка скрыть нестандартную категорию ${categoryToHide.name}")
                return false
            }
            
            Log.d(TAG, "hideDefaultCategory: попытка скрыть категорию '${categoryToHide.name}' (ID: $categoryId)")
            
            // Проверяем, сколько видимых стандартных категорий останется после скрытия
            val visibleDefaultCategories = allCategories.filter { 
                it.isDefault && !it.isHidden && it.id != categoryId 
            }
            
            if (visibleDefaultCategories.isEmpty()) {
                Log.e(TAG, "hideDefaultCategory: нельзя скрыть последнюю стандартную категорию")
                return false
            }
            
            // Пробуем обновить категорию напрямую через метод updateCategoryHiddenStatus
            val updateResult = updateCategoryHiddenStatus(categoryId, true, categoryToHide.name)
            if (updateResult) {
                return true
            }
            
            // Если обновление не сработало, пробуем специальный API метод для скрытия
            Log.d(TAG, "hideDefaultCategory: прямое обновление не сработало, пробуем специальный метод")
            
            return try {
                val request = HideCategoryRequest(categoryId = categoryId)
                Log.d(TAG, "hideDefaultCategory: запрос на скрытие категории через hideCategory: $request")
                
                val response = categoryApi.hideCategory("Bearer $token", request)
                
                if (response.isSuccessful) {
                    val baseResponse = response.body()
                    if (baseResponse?.success == true) {
                        Log.d(TAG, "hideDefaultCategory: категория успешно скрыта, сообщение: ${baseResponse.message}")
                        // Очищаем кэш, т.к. категории изменились
                        clearCache()
                        true
                    } else {
                        Log.e(TAG, "hideDefaultCategory: сервер вернул успешный статус, но операция не удалась: ${baseResponse?.message}")
                        false
                    }
                } else {
                    val errorCode = response.code()
                    val errorBody = try {
                        response.errorBody()?.string() ?: "Неизвестная ошибка"
                    } catch (e: Exception) {
                        "Ошибка при чтении тела ответа: ${e.message}"
                    }
                    Log.e(TAG, "hideDefaultCategory: ошибка API hideCategory - код: $errorCode, тело: $errorBody")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "hideDefaultCategory: исключение при вызове hideCategory", e)
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "hideDefaultCategory: общее исключение", e)
            return false
        }
    }
    
    suspend fun showDefaultCategory(categoryId: String): Boolean {
        val token = tokenManager.getToken() ?: return false
        
        try {
            // Идентифицируем категорию
            val allCategories = getAllCategoriesWithHidden()
            val categoryToShow = allCategories.find { it.id == categoryId }
            
            if (categoryToShow == null) {
                Log.e(TAG, "showDefaultCategory: категория с ID $categoryId не найдена")
                return false
            }
            
            // Проверяем, что категория действительно стандартная
            if (!categoryToShow.isDefault) {
                Log.e(TAG, "showDefaultCategory: попытка отобразить нестандартную категорию ${categoryToShow.name}")
                return false
            }
            
            Log.d(TAG, "showDefaultCategory: попытка отобразить категорию '${categoryToShow.name}' (ID: $categoryId)")
            
            // Сначала пробуем через обновление статуса
            val updateResult = updateCategoryHiddenStatus(categoryId, false, categoryToShow.name)
            if (updateResult) {
                return true
            }
            
            // Если обновление не сработало, пробуем через специальный API метод
            Log.d(TAG, "showDefaultCategory: прямое обновление не сработало, пробуем специальный метод")
            
            return try {
                val response = categoryApi.unhideCategory("Bearer $token", categoryId)
                
                if (response.isSuccessful) {
                    val baseResponse = response.body()
                    if (baseResponse?.success == true) {
                        Log.d(TAG, "showDefaultCategory: категория успешно отображена, сообщение: ${baseResponse.message}")
                        // Очищаем кэш, т.к. категории изменились
                        clearCache()
                        true
                    } else {
                        Log.e(TAG, "showDefaultCategory: сервер вернул успешный статус, но операция не удалась: ${baseResponse?.message}")
                        false
                    }
                } else {
                    val errorCode = response.code()
                    val errorBody = try {
                        response.errorBody()?.string() ?: "Неизвестная ошибка"
                    } catch (e: Exception) {
                        "Ошибка при чтении тела ответа: ${e.message}"
                    }
                    Log.e(TAG, "showDefaultCategory: ошибка API unhideCategory - код: $errorCode, тело: $errorBody")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "showDefaultCategory: исключение при вызове unhideCategory", e)
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "showDefaultCategory: общее исключение", e)
            return false
        }
    }
    
    // Метод для обновления статуса стандартных категорий в базе данных
    suspend fun updateDefaultCategoriesStatus(): Boolean {
        val token = tokenManager.getToken() ?: return false
        var success = true
        
        try {
            Log.d(TAG, "updateDefaultCategoriesStatus: начало обновления статусов стандартных категорий")
            
            // Получаем все категории
            val allCategories = try {
                val response = categoryApi.getAllCategories("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!.categories
                } else {
                    Log.e(TAG, "updateDefaultCategoriesStatus: ошибка получения категорий: ${response.code()}")
                    return false
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateDefaultCategoriesStatus: исключение при получении категорий", e)
                return false
            }
            
            // Фильтруем категории по стандартным именам, которые еще не отмечены как стандартные
            val categoriesToUpdate = allCategories.filter { 
                TaskCategory.DEFAULT_CATEGORY_NAMES.contains(it.name) && !it.isDefault 
            }
            
            Log.d(TAG, "updateDefaultCategoriesStatus: найдено ${categoriesToUpdate.size} категорий для обновления")
            
            // Обновляем статус только тех категорий, которые имеют стандартные имена, но флаг isDefault = false
            for (category in categoriesToUpdate) {
                Log.d(TAG, "updateDefaultCategoriesStatus: обновление категории ${category.name} (${category.id})")
                
                val request = CategoryUpdateRequest(
                    id = category.id,
                    // Сохраняем текущие значения для остальных полей
                    isHidden = category.isHidden,
                    name = category.name,
                    // Устанавливаем isDefault=true только для категорий с именами из DEFAULT_CATEGORY_NAMES
                    isDefault = true
                )
                
                try {
                    Log.d(TAG, "updateDefaultCategoriesStatus: отправка запроса для категории ${category.name}: $request")
                    val response = categoryApi.updateCategory("Bearer $token", request)
                    
                    if (response.isSuccessful) {
                        Log.d(TAG, "updateDefaultCategoriesStatus: категория ${category.name} успешно обновлена")
                    } else {
                        val errorCode = response.code()
                        val errorBody = response.errorBody()?.string() ?: "Неизвестная ошибка"
                        Log.e(TAG, "updateDefaultCategoriesStatus: ошибка обновления категории ${category.name}: код $errorCode, ответ: $errorBody")
                        success = false
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "updateDefaultCategoriesStatus: исключение при обновлении категории ${category.name}", e)
                    success = false
                }
            }
            
            // Очищаем кэш, т.к. категории могли измениться
            if (success) {
                clearCache()
            }
            
            return success
        } catch (e: Exception) {
            Log.e(TAG, "updateDefaultCategoriesStatus: общее исключение", e)
            return false
        }
    }
    
    // Преобразование CategoryResponse в TaskCategory с возможностью принудительно установить isDefault
    private fun CategoryResponse.toTaskCategory(forceDefault: Boolean = false): TaskCategory {
        // Если категория имеет имя из списка стандартных, проверяем, что флаг isDefault установлен
        val isStandardByName = TaskCategory.DEFAULT_CATEGORY_NAMES.contains(name)
        
        return TaskCategory(
            id = id,
            name = name,
            isDefault = isDefault || forceDefault || isStandardByName,
            isHidden = isHidden
        )
    }
} 