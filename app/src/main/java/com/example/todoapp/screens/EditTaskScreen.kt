package com.example.todoapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import com.example.todoapp.components.TaskEditContent
import com.example.todoapp.model.Task
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.model.TaskPriority
import com.example.todoapp.viewmodel.TaskViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    navController: NavController,
    tasks: List<Task>,
    viewModel: TaskViewModel,
    backStackEntry: NavBackStackEntry
) {
    val taskId = backStackEntry.arguments?.getInt("taskId") ?: return
    val task = tasks.find { it.id == taskId } ?: return

    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }
    var category by remember { mutableStateOf(task.category) }
    var deadline by remember { mutableStateOf(task.deadline) }

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

//            с апи
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

//            без апи
//            onSave = {
//                viewModel.updateTasks(
//                    tasks.map {
//                        if (it.id == taskId) {
//                            it.copy(
//                                title = title,
//                                description = description,
//                                priority = priority,
//                                category = category,
//                                deadline = deadline
//                            )
//                        } else it
//                    }
//                )
//                navController.navigateUp()
//            },
            buttonText = "Сохранить изменения"
        )
    }
}
