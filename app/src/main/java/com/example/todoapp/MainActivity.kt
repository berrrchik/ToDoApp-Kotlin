package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.ui.theme.ToDoAppTheme
import com.example.todoapp.viewmodel.CategoryViewModel
import com.example.todoapp.viewmodel.CategoryViewModelFactory
import com.example.todoapp.viewmodel.TaskViewModel
import com.example.todoapp.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(application))
            val categoryViewModel: CategoryViewModel = viewModel(factory = CategoryViewModelFactory(application))
            
            ToDoAppTheme(darkTheme = taskViewModel.isDarkTheme.collectAsState().value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TodoApp(taskViewModel, categoryViewModel)
                }
            }
        }
    }
}