package com.example.todoapp.api

import com.example.todoapp.model.BaseResponse
import com.example.todoapp.model.category.CategoryCreateRequest
import com.example.todoapp.model.category.CategoryListResponse
import com.example.todoapp.model.category.CategoryResponse
import com.example.todoapp.model.category.CategoryUpdateRequest
import com.example.todoapp.model.category.HideCategoryRequest
import retrofit2.Response
import retrofit2.http.*

interface CategoryApi {
    @GET("categories")
    suspend fun getAllCategories(
        @Header("Authorization") token: String
    ): Response<CategoryListResponse>
    
    @POST("categories")
    suspend fun createCategory(
        @Header("Authorization") token: String, 
        @Body request: CategoryCreateRequest
    ): Response<CategoryResponse>
    
    @GET("categories/{id}")
    suspend fun getCategoryById(
        @Header("Authorization") token: String, 
        @Path("id") id: String
    ): Response<CategoryResponse>
    
    @PUT("categories")
    suspend fun updateCategory(
        @Header("Authorization") token: String, 
        @Body request: CategoryUpdateRequest
    ): Response<CategoryResponse>
    
    @DELETE("categories/{id}")
    suspend fun deleteCategoryByDelete(
        @Header("Authorization") token: String, 
        @Path("id") id: String
    ): Response<BaseResponse>
    
    @POST("categories/delete/{id}")
    suspend fun deleteCategoryByPost(
        @Header("Authorization") token: String, 
        @Path("id") id: String
    ): Response<BaseResponse>
    
    @POST("categories/hide")
    suspend fun hideCategory(
        @Header("Authorization") token: String, 
        @Body request: HideCategoryRequest
    ): Response<BaseResponse>
    
    @POST("categories/unhide/{id}")
    suspend fun unhideCategory(
        @Header("Authorization") token: String, 
        @Path("id") id: String
    ): Response<BaseResponse>
    
    @GET("categories/hidden")
    suspend fun getHiddenCategories(
        @Header("Authorization") token: String
    ): Response<CategoryListResponse>
} 