package com.example.todoapp.model

data class TaskCategory(
    val id: String,
    val name: String,
    val isDefault: Boolean = false,
    val isHidden: Boolean = false
) {
    companion object {
        // Это примеры названий стандартных категорий, но их ID могут отличаться на сервере
        val DEFAULT_CATEGORY_NAMES = listOf("ДОМ", "РАБОТА", "УЧЁБА", "ДРУГОЕ")
        
        // Примеры категорий для предварительного отображения в UI
        // Фактические ID будут заменены на значения с сервера после загрузки
        val DEFAULT_CATEGORIES = listOf(
            TaskCategory("1", "ДОМ", true),
            TaskCategory("2", "РАБОТА", true),
            TaskCategory("3", "УЧЁБА", true),
            TaskCategory("4", "ДРУГОЕ", true)
        )
        
        // Возвращает категорию для использования по умолчанию
        fun getDefaultCategory(): TaskCategory = TaskCategory("1", "ДОМ", true)
    }
    
    override fun toString(): String {
        return name
    }
}