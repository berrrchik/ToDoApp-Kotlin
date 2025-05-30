package com.example.todoapp.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import com.example.todoapp.components.TaskEditContent
import com.example.todoapp.model.Task
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.viewmodel.CategoryViewModel
import com.example.todoapp.viewmodel.TaskViewModel

private const val TAG = "EditTaskScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    navController: NavController,
    tasks: List<Task>,
    viewModel: TaskViewModel,
    categoryViewModel: CategoryViewModel,
    backStackEntry: NavBackStackEntry
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val allCategories by categoryViewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val categoriesLoading by categoryViewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val categoryError by categoryViewModel.errorMessage.collectAsState()

    val taskId = backStackEntry.arguments?.getString("taskId") ?: return
    val task = tasks.find { it.id == taskId } ?: return
    
    // Snackbar для отображения ошибок
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Показываем сообщение об ошибке в Snackbar
    LaunchedEffect(errorMessage, categoryError) {
        when {
            errorMessage != null -> {
                snackbarHostState.showSnackbar(errorMessage ?: "")
                viewModel.clearError()
            }
            categoryError != null -> {
                snackbarHostState.showSnackbar(categoryError ?: "")
                categoryViewModel.clearError()
            }
        }
    }

    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }
    var category by remember { mutableStateOf(task.category) }
    var deadline by remember { mutableStateOf(task.deadline) }
    
    // Загружаем категории при первом запуске
    LaunchedEffect(Unit) {
        Log.d(TAG, "EditTaskScreen: Экран открыт, загрузка категорий")
        categoryViewModel.loadCategories()
    }
    
    // При закрытии композиции
    DisposableEffect(Unit) {
        onDispose {
            Log.d(TAG, "EditTaskScreen: Экран закрывается")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактировать задачу") },
                navigationIcon = {
                    IconButton(onClick = { 
                        Log.d(TAG, "EditTaskScreen: Нажата кнопка назад")
                        navController.navigateUp() 
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (categoriesLoading) {
                // Показываем индикатор загрузки, пока категории загружаются
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (allCategories.isEmpty()) {
                // Если категории не загрузились, показываем сообщение об ошибке
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Не удалось загрузить категории")
                        Button(onClick = { categoryViewModel.loadCategories() }) {
                            Text("Повторить")
                        }
                    }
                }
            } else {
                TaskEditContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = if (isLandscape) 32.dp else 16.dp),
                    title = title,
                    onTitleChange = { title = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    priority = priority,
                    onPriorityChange = { priority = it },
                    category = category,
                    onCategoryChange = { category = it },
                    deadline = deadline,
                    onDeadlineChange = { deadline = it },
                    onSave = {
                        if (title.isBlank()) {
                            Log.d(TAG, "EditTaskScreen: Пустой заголовок задачи, отмена обновления")
                            return@TaskEditContent
                        }
                        
                        Log.d(TAG, "EditTaskScreen: Обновление задачи '${task.id}' с категорией ${category.id}")
                        viewModel.updateTask(
                            task.copy(
                                title = title,
                                description = description,
                                priority = priority,
                                category = category,
                                deadline = deadline
                            )
                        )
                        navController.navigateUp()
                    },
                    buttonText = "Сохранить изменения",
                    categories = categoryViewModel.getVisibleCategories(),
                    isLoading = isLoading
                )
            }
        }
    }
}
