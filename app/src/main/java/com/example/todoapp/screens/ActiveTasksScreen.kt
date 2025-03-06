package com.example.todoapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todoapp.components.SearchBar
import com.example.todoapp.components.TaskItem
import com.example.todoapp.model.Task
import com.example.todoapp.navigation.Screen
import com.example.todoapp.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveTasksScreen(
    navController: NavController,
    viewModel: TaskViewModel,
    searchQuery: String,
    tasks: List<Task>
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Активные задачи") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.DeletedTasks.route) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удаленные задачи")
                        }
                        IconButton(onClick = { navController.navigate(Screen.CompletedTasks.route) }) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Выполненные задачи")
                        }
                    }
                )
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTask.route) },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
            }
        }
    ) { padding ->
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет активных задач",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            val filteredTasks = tasks.filterNot { it.isCompleted || it.isDeleted }
                .filter {
                    searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true) ||
                            it.description.contains(searchQuery, ignoreCase = true)
                }

            if (filteredTasks.isEmpty() && searchQuery.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ничего не найдено",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(filteredTasks.size) { index ->
                        val task = filteredTasks[index]
                        TaskItem(
                            task = task,
                            onEditClick = {
                                navController.navigate(Screen.EditTask.createRoute(task.id))
                            },

                            onDeleteClick = {

//                                viewModel.updateTasks(
//                                    tasks.map {
//                                        if (it.id == task.id) it.copy(isDeleted = true) else it
//                                    }
//                                )
//                                        viewModel.deleteTask(task.id)

                                viewModel.updateTask(task.copy(isDeleted = true))
                            },

                            onCompleteClick = {

//                                viewModel.updateTasks(
//                                    tasks.map {
//                                        if (it.id == task.id) it.copy(isCompleted = true) else it
//                                    }

                                viewModel.updateTask(task.copy(isCompleted = true))
                            }
                        )
                    }
                }
            }
        }
    }
}
