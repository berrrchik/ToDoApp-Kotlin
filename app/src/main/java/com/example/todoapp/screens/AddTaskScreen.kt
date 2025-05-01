package com.example.todoapp.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.СРЕДНИЙ) }
    var category by remember { mutableStateOf(TaskCategory.getDefaultCategory()) }
    var deadline by remember { mutableStateOf<LocalDateTime?>(null) }

    // Загружаем категории при первом запуске
    LaunchedEffect(Unit) {
        categoryViewModel.loadCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая задача") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                    if (title.isNotBlank()) {
                        val newTask = Task(
                            id = 0,
                            title = title,
                            description = description,
                            priority = priority,
                            category = category,
                            deadline = deadline,
                            isCompleted = false
                        )
                        viewModel.createTask(newTask)
                        navController.navigateUp()
                    }
                },
                buttonText = "Создать задачу",
                categories = allCategories
            )
        }
    }
}
