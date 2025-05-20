package com.example.todoapp.api

import com.example.todoapp.model.AuthRequest
import com.example.todoapp.model.AuthResponse
import com.example.todoapp.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(
        @Body request: AuthRequest
    ): Response<AuthResponse>
    
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>
} 