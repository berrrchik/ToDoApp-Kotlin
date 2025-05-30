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
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletedTasksScreen(
    navController: NavController,
    viewModel: TaskViewModel,
    tasks: List<Task>
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Локальное состояние для хранения удаленных задач
    var deletedTasks by remember { mutableStateOf(tasks.filter { it.isDeleted }) }
    
    // Обновляем локальное состояние, когда меняется список задач
    LaunchedEffect(tasks) {
        deletedTasks = tasks.filter { it.isDeleted }
    }
    
    // Отслеживаем события удаления задач
    LaunchedEffect(Unit) {
        viewModel.taskDeletedEvent.collectLatest { deletedTaskId ->
            // Обновляем локальное состояние, удаляя задачу
            deletedTasks = deletedTasks.filterNot { it.id == deletedTaskId }
        }
    }
    
    // SnackBar для отображения ошибок
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (deletedTasks.isEmpty()) {
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
                                onDeleteClick = { viewModel.permanentlyDeleteTask(task.id) },
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
                            onDeleteClick = { viewModel.permanentlyDeleteTask(task.id) },
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
