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
import androidx.navigation.NavBackStackEntry
import com.example.todoapp.components.TaskEditContent
import com.example.todoapp.model.Task
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.viewmodel.CategoryViewModel
import com.example.todoapp.viewmodel.TaskViewModel

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

    val taskId = backStackEntry.arguments?.getInt("taskId") ?: return
    val task = tasks.find { it.id == taskId } ?: return

    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }
    var category by remember { mutableStateOf(task.category) }
    var deadline by remember { mutableStateOf(task.deadline) }
    
    // Загружаем категории при первом запуске
    LaunchedEffect(Unit) {
        categoryViewModel.loadCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактировать задачу") },
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
                categories = allCategories
            )
        }
    }
}
