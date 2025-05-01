package com.example.todoapp

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoapp.navigation.Screen
import com.example.todoapp.screens.ActiveTasksScreen
import com.example.todoapp.screens.AddTaskScreen
import com.example.todoapp.screens.CompletedTasksScreen
import com.example.todoapp.screens.DeletedTasksScreen
import com.example.todoapp.screens.EditTaskScreen
import com.example.todoapp.screens.LoginScreen
import com.example.todoapp.screens.RegisterScreen
import com.example.todoapp.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(viewModel: TaskViewModel) {
    val navController = rememberNavController()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        enterTransition = { slideInHorizontally() },
        exitTransition = { slideOutHorizontally() }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.ActiveTasks.route) {
            ActiveTasksScreen(navController, viewModel, searchQuery, tasks)
        }
        composable(Screen.CompletedTasks.route) {
            CompletedTasksScreen(navController, viewModel, tasks)
        }
        composable(Screen.DeletedTasks.route) {
            DeletedTasksScreen(navController, viewModel, tasks)
        }
        composable(Screen.EditTask.route, arguments = listOf(navArgument("taskId") { type = NavType.IntType })) { backStackEntry ->
            EditTaskScreen(navController, tasks, viewModel, backStackEntry)
        }
        composable(Screen.AddTask.route) {
            AddTaskScreen(navController, viewModel)
        }
    }
}
