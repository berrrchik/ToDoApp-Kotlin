package com.example.todoapp.repository

import android.content.Context
import android.util.Log
import com.example.todoapp.api.ApiClient
import com.example.todoapp.model.Task
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.model.TaskPriority
import com.example.todoapp.model.task.TaskCreateRequest
import com.example.todoapp.model.task.TaskResponse
import com.example.todoapp.model.task.TaskUpdateRequest
import com.example.todoapp.utils.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TaskRepository(private val context: Context) {
    private val taskApi = ApiClient.taskApi
    private val tokenManager = TokenManager(context)
    private val categoryRepository = CategoryRepository(context)
    private val gson = Gson()
    
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val TAG = "TaskRepository"
    
    suspend fun getAllTasks(): List<Task> {
        val token = tokenManager.getToken()
        
        if (token == null) {
            Log.e(TAG, "getAllTasks: токен отсутствует")
            return emptyList()
        }
        
        return try {
            Log.d(TAG, "getAllTasks: запрос на сервер")
            val response = taskApi.getAllTasks("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val tasks = response.body()!!.tasks.map { it.toTaskModel(categoryRepository) }
                Log.d(TAG, "getAllTasks: получено ${tasks.size} задач")
                tasks
            } else {
                Log.e(TAG, "getAllTasks: ошибка ${response.code()} - ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllTasks: исключение", e)
            emptyList()
        }
    }
    
    suspend fun createTask(task: Task): Task? {
        val token = tokenManager.getToken()
        
        if (token == null) {
            Log.e(TAG, "createTask: токен отсутствует")
            return null
        }
        
        return try {
            // Загружаем существующие категории с сервера, чтобы убедиться,
            // что выбранная категория существует на сервере
            val serverCategories = categoryRepository.getAllCategories()
            
            // Проверяем, есть ли выбранная категория на сервере
            val categoryExists = serverCategories.any { it.id == task.category.id }
            val categoryToUse = if (categoryExists) {
                task.category
            } else {
                // Если категории нет, используем первую доступную или создаем базовую
                Log.w(TAG, "createTask: выбранная категория с ID=${task.category.id} не найдена на сервере")
                serverCategories.firstOrNull() ?: TaskCategory.getDefaultCategory()
            }
            
            Log.d(TAG, "createTask: используем категорию $categoryToUse")
            
            val request = TaskCreateRequest(
                title = task.title,
                description = task.description,
                priority = task.priority.name,
                categoryId = categoryToUse.id,
                deadline = task.deadline?.format(dateTimeFormatter)
            )
            
            Log.d(TAG, "createTask: запрос на сервер с данными $request")
            val response = taskApi.createTask("Bearer $token", request)
            
            Log.d(TAG, "createTask: получен ответ с кодом ${response.code()}")
            if (response.isSuccessful && response.body() != null) {
                val createdTask = response.body()!!.toTaskModel(categoryRepository)
                Log.d(TAG, "createTask: успешно создана задача $createdTask")
                createdTask
            } else {
                val errorBody = response.errorBody()?.string() ?: "Пустое тело ошибки"
                Log.e(TAG, "createTask: ошибка ${response.code()} - $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "createTask: исключение", e)
            null
        }
    }
    
    suspend fun updateTask(task: Task): Task? {
        val token = tokenManager.getToken()
        
        if (token == null) {
            Log.e(TAG, "updateTask: токен отсутствует")
            return null
        }
        
        return try {
            // Проверки и логирование для отладки
            Log.d(TAG, "updateTask: НАЧАЛО ДИАГНОСТИКИ ===========================")
            
            // 1. Проверка ID задачи
            Log.d(TAG, "updateTask: ID задачи: '${task.id}'")
            if (task.id.isBlank()) {
                Log.e(TAG, "updateTask: ОШИБКА - ID задачи пустой!")
            }
            
            // 2. Проверка значения priority
            Log.d(TAG, "updateTask: Проверка priority = ${task.priority.name}")
            val validPriorities = TaskPriority.values().map { it.name }
            if (!validPriorities.contains(task.priority.name)) {
                Log.e(TAG, "updateTask: ОШИБКА - неверное значение priority! Допустимые значения: $validPriorities")
            }
            
            // 3. Проверка формата даты deadline
            if (task.deadline != null) {
                try {
                    val formattedDeadline = task.deadline.format(dateTimeFormatter)
                    Log.d(TAG, "updateTask: Дата deadline в ISO формате: '$formattedDeadline'")
                    // Проверка обратного парсинга для валидации
                    LocalDateTime.parse(formattedDeadline, dateTimeFormatter)
                    Log.d(TAG, "updateTask: Формат даты deadline корректный")
                } catch (e: Exception) {
                    Log.e(TAG, "updateTask: ОШИБКА форматирования даты deadline", e)
                }
            } else {
                Log.d(TAG, "updateTask: Дата deadline не задана (null)")
            }
            
            // 4. Проверка категории
            Log.d(TAG, "updateTask: Категория задачи: ${task.category}")
            Log.d(TAG, "updateTask: ID категории: '${task.category.id}'")
            
            // Аналогично createTask, проверяем существование категории перед отправкой
            val serverCategories = categoryRepository.getAllCategories()
            Log.d(TAG, "updateTask: Доступные категории на сервере: ${serverCategories.map { "${it.id}:${it.name}" }}")
            
            val categoryExists = serverCategories.any { it.id == task.category.id }
            Log.d(TAG, "updateTask: Категория существует на сервере: $categoryExists")
            
            val categoryToUse = if (categoryExists) {
                task.category
            } else {
                Log.w(TAG, "updateTask: ВНИМАНИЕ - выбранная категория с ID=${task.category.id} не найдена на сервере")
                val fallbackCategory = serverCategories.firstOrNull() ?: TaskCategory.getDefaultCategory()
                Log.d(TAG, "updateTask: Используем запасную категорию: $fallbackCategory")
                fallbackCategory
            }
            
            val request = TaskUpdateRequest(
                id = task.id,
                title = task.title,
                description = task.description,
                isCompleted = task.isCompleted,
                isDeleted = task.isDeleted,
                priority = task.priority.name,
                categoryId = categoryToUse.id,
                deadline = task.deadline?.format(dateTimeFormatter)
            )
            
            // 5. Логирование полного тела запроса в JSON формате
            val requestJson = gson.toJson(request)
            Log.d(TAG, "updateTask: Полное тело запроса (JSON): $requestJson")
            Log.d(TAG, "updateTask: КОНЕЦ ДИАГНОСТИКИ ===========================")
            
            Log.d(TAG, "updateTask: Отправка запроса на обновление задачи с ID ${task.id}")
            val response = taskApi.updateTask("Bearer $token", request)
            
            if (response.isSuccessful && response.body() != null) {
                val updatedTask = response.body()!!.toTaskModel(categoryRepository)
                Log.d(TAG, "updateTask: Успешно обновлена задача $updatedTask")
                updatedTask
            } else {
                val errorCode = response.code()
                val errorBody = response.errorBody()?.string() ?: "Пустое тело ошибки"
                Log.e(TAG, "updateTask: Ошибка $errorCode - $errorBody")
                
                // Дополнительная отладочная информация при ошибке
                if (errorCode == 400) {
                    Log.e(TAG, "updateTask: Получена ошибка 400 Bad Request. Возможные причины:")
                    Log.e(TAG, "updateTask: 1. Неверный формат priority (допустимые: НИЗКИЙ, СРЕДНИЙ, ВЫСОКИЙ)")
                    Log.e(TAG, "updateTask: 2. Неверный формат даты deadline (требуется ISO формат)")
                    Log.e(TAG, "updateTask: 3. Неверное значение categoryId")
                    Log.e(TAG, "updateTask: 4. Неверный формат JSON")
                }
                
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateTask: Исключение при обновлении задачи", e)
            null
        }
    }
    
    suspend fun deleteTask(taskId: String): Boolean {
        val token = tokenManager.getToken()
        
        if (token == null) {
            Log.e(TAG, "deleteTask: токен отсутствует")
            return false
        }
        
        return try {
            Log.d(TAG, "deleteTask: запрос на удаление задачи с ID $taskId")
            val response = taskApi.deleteTask("Bearer $token", taskId)
            
            if (response.isSuccessful) {
                Log.d(TAG, "deleteTask: успешно удалена задача с ID $taskId")
                true
            } else {
                Log.e(TAG, "deleteTask: ошибка ${response.code()} - ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "deleteTask: исключение", e)
            false
        }
    }
    
    suspend fun permanentlyDeleteTask(taskId: String): Boolean {
        val token = tokenManager.getToken()
        
        if (token == null) {
            Log.e(TAG, "permanentlyDeleteTask: токен отсутствует")
            return false
        }
        
        return try {
            Log.d(TAG, "permanentlyDeleteTask: запрос на полное удаление задачи с ID $taskId")
            val response = taskApi.permanentlyDeleteTask("Bearer $token", taskId)
            
            // Если код 200 - стандартная успешная обработка
            if (response.isSuccessful) {
                val baseResponse = response.body()
                if (baseResponse?.success == true) {
                    Log.d(TAG, "permanentlyDeleteTask: задача успешно удалена из базы данных $taskId")
                    return true
                } else {
                    Log.e(TAG, "permanentlyDeleteTask: ошибка в ответе сервера: ${baseResponse?.message}")
                    return false
                }
            } 
            // Если код 500 - сервер отдает ошибку, но фактически задача удаляется
            else if (response.code() == 500) {
                Log.w(TAG, "permanentlyDeleteTask: получен код 500, но считаем задачу удаленной")
                return true
            }
            // Другие ошибки
            else {
                val errorCode = response.code()
                val errorBody = response.errorBody()?.string() ?: "Неизвестная ошибка"
                Log.e(TAG, "permanentlyDeleteTask: ошибка $errorCode - $errorBody")
                return false
            }
        } catch (e: Exception) {
            Log.e(TAG, "permanentlyDeleteTask: исключение", e)
            return false
        }
    }
    
    private suspend fun TaskResponse.toTaskModel(categoryRepository: CategoryRepository): Task {
        val category = categoryRepository.getCategoryById(categoryId) 
            ?: TaskCategory.getDefaultCategory()
            
        return Task(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            isDeleted = isDeleted,
            priority = TaskPriority.valueOf(priority),
            category = category,
            deadline = deadline?.let { LocalDateTime.parse(it, dateTimeFormatter) }
        )
    }
} 