package com.example.todoapp.model

data class RegisterRequest(
    val login: String,
    val email: String,
    val password: String
) 