package com.example.todoapp.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.todoapp.model.Task
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.model.TaskPriority
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TaskStorage(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("todo_app_tasks", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    // Ключ для хранения списка задач
    private val TASKS_KEY = "tasks_list"
    
    // Ключ для хранения последнего ID
    private val LAST_ID_KEY = "last_task_id"
    
    // Получить все задачи
    fun getAllTasks(): List<Task> {
        val tasksJson = sharedPreferences.getString(TASKS_KEY, "[]")
        val type = object : TypeToken<List<TaskDto>>() {}.type
        val taskDtos = gson.fromJson<List<TaskDto>>(tasksJson, type) ?: emptyList()
        return taskDtos.map { it.toTask() }
    }
    
    // Сохранить список задач
    private fun saveTasks(tasks: List<Task>) {
        val taskDtos = tasks.map { TaskDto.fromTask(it) }
        val tasksJson = gson.toJson(taskDtos)
        sharedPreferences.edit().putString(TASKS_KEY, tasksJson).apply()
    }
    
    // Получить следующий ID для новой задачи
    private fun getNextId(): Int {
        val lastId = sharedPreferences.getInt(LAST_ID_KEY, 0)
        val nextId = lastId + 1
        sharedPreferences.edit().putInt(LAST_ID_KEY, nextId).apply()
        return nextId
    }
    
    // Создать новую задачу
    fun createTask(task: Task): Task {
        val tasks = getAllTasks().toMutableList()
        val nextId = if (task.id == 0) getNextId() else task.id
        val newTask = task.copy(id = nextId)
        tasks.add(newTask)
        saveTasks(tasks)
        return newTask
    }
    
    // Обновить существующую задачу
    fun updateTask(task: Task): Task {
        val tasks = getAllTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            saveTasks(tasks)
            return task
        }
        return createTask(task)
    }
    
    // Удалить задачу по ID
    fun deleteTask(taskId: Int): Boolean {
        val tasks = getAllTasks().toMutableList()
        val removed = tasks.removeIf { it.id == taskId }
        if (removed) {
            saveTasks(tasks)
        }
        return removed
    }

    // DTO для сериализации/десериализации задач
    private data class TaskDto(
        val id: Int,
        val title: String,
        val description: String,
        val isCompleted: Boolean,
        val isDeleted: Boolean,
        val priority: String,
        val category: String,
        val deadline: String?
    ) {
        fun toTask(): Task {
            return Task(
                id = id,
                title = title,
                description = description,
                isCompleted = isCompleted,
                isDeleted = isDeleted,
                priority = TaskPriority.valueOf(priority),
                category = TaskCategory.valueOf(category),
                deadline = deadline?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
            )
        }
        
        companion object {
            fun fromTask(task: Task): TaskDto {
                return TaskDto(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    isCompleted = task.isCompleted,
                    isDeleted = task.isDeleted,
                    priority = task.priority.name,
                    category = task.category.name,
                    deadline = task.deadline?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }
        }
    }
} 