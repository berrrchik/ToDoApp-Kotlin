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
import com.example.todoapp.components.TaskEditContent
import com.example.todoapp.model.Task
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.model.TaskPriority
import com.example.todoapp.viewmodel.CategoryViewModel
import com.example.todoapp.viewmodel.TaskViewModel
import java.time.LocalDateTime

private const val TAG = "AddTaskScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navController: NavController, 
    viewModel: TaskViewModel,
    categoryViewModel: CategoryViewModel
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val allCategories by categoryViewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val categoriesLoading by categoryViewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val categoryError by categoryViewModel.errorMessage.collectAsState()
    
    // Флаг для отслеживания, была ли создана задача
    var wasTaskCreated by remember { mutableStateOf(false) }
    
    // Загружаем категории при первом запуске
    var categoriesLoaded by remember { mutableStateOf(false) }
    
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

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.СРЕДНИЙ) }
    var category by remember { mutableStateOf<TaskCategory?>(null) }
    var deadline by remember { mutableStateOf<LocalDateTime?>(null) }

    // Загружаем категории при первом запуске
    LaunchedEffect(Unit) {
        Log.d(TAG, "AddTaskScreen: Экран открыт, загрузка категорий")
        categoryViewModel.loadCategories()
        categoriesLoaded = true
    }
    
    // При закрытии композиции
    DisposableEffect(Unit) {
        onDispose {
            Log.d(TAG, "AddTaskScreen: Экран закрывается")
        }
    }
    
    // Установка категории по умолчанию после загрузки категорий
    LaunchedEffect(allCategories, categoriesLoaded) {
        if (categoriesLoaded && category == null && allCategories.isNotEmpty()) {
            Log.d(TAG, "AddTaskScreen: Категории загружены, установка категории по умолчанию ${allCategories.first()}")
            category = allCategories.first()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая задача") },
                navigationIcon = {
                    IconButton(onClick = { 
                        Log.d(TAG, "AddTaskScreen: Нажата кнопка назад")
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
            } else if (category != null) {
                // Отображаем форму создания задачи только когда категории загружены
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
                    category = category!!,
                    onCategoryChange = { category = it },
                    deadline = deadline,
                    onDeadlineChange = { deadline = it },
                    onSave = {
                        if (title.isBlank()) {
                            Log.d(TAG, "AddTaskScreen: Пустой заголовок задачи, отмена создания")
                            return@TaskEditContent
                        }
                        
                        if (category == null) {
                            Log.d(TAG, "AddTaskScreen: Категория не выбрана, отмена создания")
                            return@TaskEditContent
                        }
                        
                        Log.d(TAG, "AddTaskScreen: Создание задачи '$title' с категорией ${category?.id}")
                        val newTask = Task(
                            id = "",
                            title = title,
                            description = description,
                            priority = priority,
                            category = category!!,
                            deadline = deadline,
                            isCompleted = false
                        )
                        wasTaskCreated = true
                        Log.d(TAG, "AddTaskScreen: wasTaskCreated установлен в true")
                        viewModel.createTask(newTask)
                    },
                    buttonText = "Создать задачу",
                    categories = categoryViewModel.getVisibleCategories(),
                    isLoading = isLoading
                )
            }
        }
    }
    
    // Если задача была создана и загрузка завершена без ошибок, возвращаемся на предыдущий экран
    LaunchedEffect(isLoading, wasTaskCreated) {
        Log.d(TAG, "AddTaskScreen: LaunchedEffect isLoading=$isLoading, wasTaskCreated=$wasTaskCreated, errorMessage=$errorMessage")
        if (!isLoading && errorMessage == null && wasTaskCreated) {
            Log.d(TAG, "AddTaskScreen: Условие выполнено, выполняем navigateUp()")
            navController.navigateUp()
        }
    }
}
