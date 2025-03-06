package com.example.todoapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.todoapp.components.TaskEditContent
import com.example.todoapp.model.Task
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.model.TaskPriority
import com.example.todoapp.viewmodel.TaskViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, viewModel: TaskViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.СРЕДНИЙ) }
    var category by remember { mutableStateOf(TaskCategory.ДРУГОЕ) }
    var deadline by remember { mutableStateOf<LocalDateTime?>(null) }

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
        TaskEditContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
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
//                        id = tasks.size,

                        id = 0,
                        title = title,
                        description = description,
                        priority = priority,
                        category = category,
                        deadline = deadline,
                        isCompleted = false
                    )
//                    viewModel.updateTasks(tasks + newTask)
                    viewModel.createTask(newTask)
                    navController.navigateUp()
                }
            },
            buttonText = "Создать задачу"
        )
    }
}
