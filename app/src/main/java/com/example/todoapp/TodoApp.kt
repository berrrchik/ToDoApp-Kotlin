package com.example.todoapp

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoapp.navigation.Screen
import com.example.todoapp.screens.ActiveTasksScreen
import com.example.todoapp.screens.AddTaskScreen
import com.example.todoapp.screens.CategoriesManagementScreen
import com.example.todoapp.screens.CompletedTasksScreen
import com.example.todoapp.screens.DeletedTasksScreen
import com.example.todoapp.screens.EditTaskScreen
import com.example.todoapp.screens.LoginScreen
import com.example.todoapp.screens.RegisterScreen
import com.example.todoapp.utils.TokenManager
import com.example.todoapp.viewmodel.CategoryViewModel
import com.example.todoapp.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(
    taskViewModel: TaskViewModel,
    categoryViewModel: CategoryViewModel
) {
    val navController = rememberNavController()
    val searchQuery by taskViewModel.searchQuery.collectAsState()
    val tasks by taskViewModel.tasks.collectAsState()
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    
    // При входе в приложение загружаем задачи, если пользователь авторизован
    LaunchedEffect(Unit) {
        if (tokenManager.isLoggedIn()) {
            taskViewModel.loadTasks()
            categoryViewModel.loadCategories()
        }
    }
    
    val startDestination = if (tokenManager.isLoggedIn()) {
        Screen.ActiveTasks.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally() },
        exitTransition = { slideOutHorizontally() }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController, taskViewModel, categoryViewModel)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController, taskViewModel, categoryViewModel)
        }
        composable(Screen.ActiveTasks.route) {
            // При переходе на экран активных задач загружаем их
            LaunchedEffect(Unit) {
                taskViewModel.loadTasks()
            }
            ActiveTasksScreen(navController, taskViewModel, searchQuery, tasks, categoryViewModel)
        }
        composable(Screen.CompletedTasks.route) {
            CompletedTasksScreen(navController, taskViewModel, tasks)
        }
        composable(Screen.DeletedTasks.route) {
            DeletedTasksScreen(navController, taskViewModel, tasks)
        }
        composable(Screen.EditTask.route, arguments = listOf(navArgument("taskId") { type = NavType.StringType })) { backStackEntry ->
            EditTaskScreen(navController, tasks, taskViewModel, categoryViewModel, backStackEntry)
        }
        composable(Screen.AddTask.route) {
            AddTaskScreen(navController, taskViewModel, categoryViewModel)
        }
        composable(Screen.Categories.route) {
            CategoriesManagementScreen(navController, categoryViewModel)
        }
    }
}
