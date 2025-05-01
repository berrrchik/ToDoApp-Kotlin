package com.example.todoapp.model

data class TaskCategory(
    val id: Int,
    val name: String,
    val isDefault: Boolean = false,
    val isHidden: Boolean = false
) {
    companion object {
        val DEFAULT_CATEGORIES = listOf(
            TaskCategory(1, "ДОМ", true),
            TaskCategory(2, "РАБОТА", true),
            TaskCategory(3, "УЧЁБА", true),
            TaskCategory(4, "ДРУГОЕ", true)
        )
        
        fun getDefaultCategory(): TaskCategory = DEFAULT_CATEGORIES.last()
    }
    
    override fun toString(): String {
        return name
    }
}