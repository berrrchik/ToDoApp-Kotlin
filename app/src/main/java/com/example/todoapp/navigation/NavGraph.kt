package com.example.todoapp.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ActiveTasks : Screen("active_tasks")
    object CompletedTasks : Screen("completed_tasks")
    object DeletedTasks : Screen("deleted_tasks")
    object AddTask : Screen("add_task")
    object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: Int) = "edit_task/$taskId"
    }
    object Categories : Screen("categories")
} 