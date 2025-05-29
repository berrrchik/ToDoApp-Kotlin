package com.example.todoapp.api

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"  // IP для доступа к localhost с эмулятора
    private const val TAG = "ApiClient"
    
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Создаем более толерантный Gson
    private val gson = GsonBuilder()
        .setLenient() // Делаем Gson более терпимым к неправильным JSON
        .create()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val taskApi: TaskApi = retrofit.create(TaskApi::class.java)
    val categoryApi: CategoryApi = retrofit.create(CategoryApi::class.java)
} 