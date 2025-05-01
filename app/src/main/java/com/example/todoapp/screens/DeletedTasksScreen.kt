package com.example.todoapp.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController
import com.example.todoapp.components.TaskItem
import com.example.todoapp.model.Task
import com.example.todoapp.navigation.Screen
import com.example.todoapp.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletedTasksScreen(
    navController: NavController,
    viewModel: TaskViewModel,
    tasks: List<Task>
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val deletedTasks = tasks.filter { it.isDeleted }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Удаленные задачи") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        if (deletedTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет удаленных задач",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(deletedTasks) { task ->
                            TaskItem(
                                task = task,
                                onEditClick = { navController.navigate(Screen.EditTask.createRoute(task.id)) },
                                onDeleteClick = { viewModel.deleteTask(task.id) },
                                onCompleteClick = { viewModel.updateTask(task.copy(isDeleted = false)) },
                                completeIcon = Icons.Default.Undo,
                                completeIconDescription = "Восстановить"
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(deletedTasks) { task ->
                        TaskItem(
                            task = task,
                            onEditClick = { navController.navigate(Screen.EditTask.createRoute(task.id)) },
                            onDeleteClick = { viewModel.deleteTask(task.id) },
                            onCompleteClick = { viewModel.updateTask(task.copy(isDeleted = false)) },
                            completeIcon = Icons.Default.Undo,
                            completeIconDescription = "Восстановить"
                        )
                    }
                }
            }
        }
    }
}
