package com.example.todoapp.model.category

data class CategoryCreateRequest(
    val name: String
)

data class CategoryUpdateRequest(
    val id: String,
    val name: String? = null,
    val isHidden: Boolean? = null,
    val isDefault: Boolean? = null
)

data class CategoryResponse(
    val id: String,
    val name: String,
    val isDefault: Boolean,
    val isHidden: Boolean,
    val createdAt: String
)

data class CategoryListResponse(
    val categories: List<CategoryResponse>
)

data class HideCategoryRequest(
    val categoryId: String
) 