package com.example.todoapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.todoapp.components.TaskItem
import com.example.todoapp.model.Task
import com.example.todoapp.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletedTasksScreen(navController: NavController, tasks: List<Task>, viewModel: TaskViewModel) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(tasks.filter { it.isDeleted }.size) { index ->
                val task = tasks.filter { it.isDeleted }[index]
                TaskItem(
                    task = task,
                    onEditClick = { },
                    onDeleteClick = {

//                        viewModel.updateTasks(
//                                    tasks.filterNot { it.id == task.id }
//                                )

                        viewModel.deleteTask(task.id)
                    },
                    onCompleteClick = {
//                        viewModel.updateTasks(
//                                    tasks.map {
//                                        if (it.id == task.id) it.copy(isDeleted = false) else it
//                                    }
                        viewModel.updateTask(task.copy(isDeleted = false))
                    },
                    completeIcon = Icons.Default.Restore,
                    completeIconDescription = "Восстановить"
                )
            }
        }
    }
}
