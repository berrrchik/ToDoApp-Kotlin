package com.example.todoapp.model.task

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class TaskCreateRequest(
    val title: String,
    val description: String,
    val priority: String,
    val categoryId: String,
    val deadline: String? = null
)

data class TaskUpdateRequest(
    val id: String,
    val title: String? = null,
    val description: String? = null,
    val isCompleted: Boolean? = null,
    val isDeleted: Boolean? = null,
    val priority: String? = null,
    val categoryId: String? = null,
    val deadline: String? = null
)

data class TaskResponse(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val isDeleted: Boolean,
    val priority: String,
    val categoryId: String,
    val deadline: String? = null,
    val createdAt: String,
    val updatedAt: String
)

data class TaskListResponse(
    val tasks: List<TaskResponse>
) 