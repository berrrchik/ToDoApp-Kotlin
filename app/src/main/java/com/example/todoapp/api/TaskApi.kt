package com.example.todoapp.api

import com.example.todoapp.model.BaseResponse
import com.example.todoapp.model.task.TaskCreateRequest
import com.example.todoapp.model.task.TaskListResponse
import com.example.todoapp.model.task.TaskResponse
import com.example.todoapp.model.task.TaskUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface TaskApi {
    @GET("tasks")
    suspend fun getAllTasks(
        @Header("Authorization") token: String
    ): Response<TaskListResponse>
    
    @POST("tasks")
    suspend fun createTask(
        @Header("Authorization") token: String, 
        @Body request: TaskCreateRequest
    ): Response<TaskResponse>
    
    @GET("tasks/{id}")
    suspend fun getTaskById(
        @Header("Authorization") token: String, 
        @Path("id") id: String
    ): Response<TaskResponse>
    
    @PUT("tasks")
    suspend fun updateTask(
        @Header("Authorization") token: String, 
        @Body request: TaskUpdateRequest
    ): Response<TaskResponse>
    
    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Header("Authorization") token: String, 
        @Path("id") id: String
    ): Response<String>
    
    @DELETE("tasks/permanently/{id}")
    suspend fun permanentlyDeleteTask(
        @Header("Authorization") token: String, 
        @Path("id") id: String
    ): Response<BaseResponse>
} 