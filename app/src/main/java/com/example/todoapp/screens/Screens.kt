//package com.example.todoapp.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.todoapp.components.*
//import com.example.todoapp.model.*
//import com.example.todoapp.navigation.Screen
//
//@Composable
//fun ActiveTasksScreen(
//    tasks: List<Task>,
//    onTaskUpdate: (List<Task>) -> Unit,
//    navController: NavController
//) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Активные задачи") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                ),
//                actions = {
//                    IconButton(onClick = { navController.navigate(Screen.DeletedTasks.route) }) {
//                        Icon(Icons.Default.Delete, contentDescription = "Удаленные задачи")
//                    }
//                    IconButton(onClick = { navController.navigate(Screen.CompletedTasks.route) }) {
//                        Icon(Icons.Default.CheckCircle, contentDescription = "Выполненные задачи")
//                    }
//                }
//            )
//        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { navController.navigate(Screen.AddTask.route) },
//                containerColor = MaterialTheme.colorScheme.primaryContainer
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
//            }
//        }
//    ) { padding ->
//        // ... код содержимого экрана ...
//    }
//}
//
//@Composable
//fun CompletedTasksScreen(
//    tasks: List<Task>,
//    onTaskUpdate: (List<Task>) -> Unit,
//    navController: NavController
//) {
//    // ... код экрана выполненных задач ...
//}
//
//@Composable
//fun DeletedTasksScreen(
//    tasks: List<Task>,
//    onTaskUpdate: (List<Task>) -> Unit,
//    navController: NavController
//) {
//    // ... код экрана удаленных задач ...
//}
//
//@Composable
//fun AddTaskScreen(
//    tasks: List<Task>,
//    onTaskUpdate: (List<Task>) -> Unit,
//    navController: NavController
//) {
//    // ... код экрана добавления задачи ...
//}
//
//@Composable
//fun EditTaskScreen(
//    taskId: Int,
//    tasks: List<Task>,
//    onTaskUpdate: (List<Task>) -> Unit,
//    navController: NavController
//) {
//    // ... код экрана редактирования задачи ...
//}