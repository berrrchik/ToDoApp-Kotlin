package com.example.todoapp.api

import com.example.todoapp.model.Task
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.model.TaskPriority
import kotlinx.coroutines.delay
import java.time.LocalDateTime

interface TaskApiService {
    suspend fun getTasks(): List<Task>
    suspend fun createTask(task: Task): Task
    suspend fun updateTask(task: Task): Task
    suspend fun deleteTask(taskId: Int): Boolean
}

// Реализация-заглушка
class MockTaskApiService : TaskApiService {
    private val mockDelay = 500L // Имитация задержки сети
    private val mockTasks = mutableListOf(
        Task(
            id = 1,
            title = "Подготовить презентацию",
            description = "Презентация по проекту на следующей неделе",
            priority = TaskPriority.HIGH,
            category = TaskCategory.WORK,
            deadline = LocalDateTime.now().plusDays(5)
        ),
        Task(
            id = 2,
            title = "Купить продукты",
            description = "Молоко, хлеб, овощи",
            priority = TaskPriority.MEDIUM,
            category = TaskCategory.HOME
        ),
        Task(
            id = 3,
            title = "Подготовиться к экзамену",
            description = "Повторить материалы по Android разработке",
            priority = TaskPriority.HIGH,
            category = TaskCategory.STUDY,
            deadline = LocalDateTime.now().plusDays(2)
        )
    )

    override suspend fun getTasks(): List<Task> {
        delay(mockDelay) // Имитация задержки сети
        return mockTasks.toList()
    }

    override suspend fun createTask(task: Task): Task {
        delay(mockDelay)
        val newTask = task.copy(id = mockTasks.size + 1)
        mockTasks.add(newTask)
        return newTask
    }

    override suspend fun updateTask(task: Task): Task {
        delay(mockDelay)
        val index = mockTasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            mockTasks[index] = task
        }
        return task
    }

    override suspend fun deleteTask(taskId: Int): Boolean {
        delay(mockDelay)
        return mockTasks.removeIf { it.id == taskId }
    }
} 