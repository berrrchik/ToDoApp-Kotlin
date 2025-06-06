package com.example.todoapp.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todoapp.components.SearchBar
import com.example.todoapp.components.TaskItem
import com.example.todoapp.model.Task
import com.example.todoapp.navigation.Screen
import com.example.todoapp.utils.TokenManager
import com.example.todoapp.viewmodel.TaskViewModel
import com.example.todoapp.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveTasksScreen(
    navController: NavController,
    viewModel: TaskViewModel,
    searchQuery: String,
    tasks: List<Task>,
    categoryViewModel: CategoryViewModel? = null
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Snackbar для отображения ошибок
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Показываем сообщение об ошибке в Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

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
                        IconButton(onClick = { navController.navigate(Screen.Categories.route) }) {
                            Icon(Icons.Default.Category, contentDescription = "Управление категориями")
                        }
                        IconButton(onClick = { viewModel.toggleTheme() }) {
                            Icon(Icons.Default.Brightness6, contentDescription = "Сменить тему")
                        }
                        IconButton(onClick = { navController.navigate(Screen.DeletedTasks.route) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удаленные задачи")
                        }
                        IconButton(onClick = { navController.navigate(Screen.CompletedTasks.route) }) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Выполненные задачи")
                        }
                        IconButton(onClick = {
                            tokenManager.clearToken()
                            // Очищаем кэш категорий при выходе
                            (categoryViewModel as? CategoryViewModel)?.clearCategoryCache() 
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }) {
                            Icon(Icons.Default.Logout, contentDescription = "Выйти")
                        }
                    }
                )
                val history by viewModel.searchHistory.collectAsState()
                val isHistoryVisible by viewModel.isHistoryVisible.collectAsState()
                val isSearching by viewModel.isSearching.collectAsState()
                
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onRefresh = { viewModel.loadTasks() },
                    isTasksEmpty = tasks.isEmpty(),
                    searchHistory = history,
                    onHistoryItemClick = { 
                        viewModel.updateSearchQuery(it)
                        viewModel.setHistoryVisible(false)
                    },
                    onClearHistoryClick = { viewModel.clearSearchHistory() },
                    isHistoryVisible = isHistoryVisible,
                    onFocusChange = { viewModel.setHistoryVisible(it) },
                    isSearching = isSearching
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
        } else if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Нет активных задач",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadTasks() }) {
                        Text("Обновить")
                    }
                    
                    // Кнопка для отладки с информацией о задачах
                    OutlinedButton(
                        onClick = {
                            viewModel.loadTasks()
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Диагностика API")
                    }
                }
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
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(filteredTasks) { task ->
                                TaskItem(
                                    task = task,
                                    onEditClick = { navController.navigate(Screen.EditTask.createRoute(task.id)) },
                                    onDeleteClick = {
                                        viewModel.updateTask(task.copy(isDeleted = true))
                                    },
                                    onCompleteClick = {
                                        viewModel.updateTask(task.copy(isCompleted = true))
                                    }
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
                        items(filteredTasks) { task ->
                            TaskItem(
                                task = task,
                                onEditClick = { navController.navigate(Screen.EditTask.createRoute(task.id)) },
                                onDeleteClick = {
                                    viewModel.updateTask(task.copy(isDeleted = true))
                                },
                                onCompleteClick = {
                                    viewModel.updateTask(task.copy(isCompleted = true))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}