package com.example.todoapp.model

import java.time.LocalDateTime

enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

enum class TaskCategory {
    HOME, WORK, STUDY, OTHER
}

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val category: TaskCategory = TaskCategory.OTHER,
    val deadline: LocalDateTime? = null
)