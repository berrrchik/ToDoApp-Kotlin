package com.example.todoapp.model

import java.time.LocalDateTime

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.СРЕДНИЙ,
    val category: TaskCategory = TaskCategory.ДРУГОЕ,
    val deadline: LocalDateTime? = null
)