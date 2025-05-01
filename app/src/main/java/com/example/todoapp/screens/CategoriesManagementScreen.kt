package com.example.todoapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesManagementScreen(
    navController: NavController,
    viewModel: CategoryViewModel
) {
    val categories by viewModel.categories.collectAsState()
    val userCategories by viewModel.userCategories.collectAsState()
    val defaultCategories by viewModel.defaultCategories.collectAsState()
    val hiddenDefaultCategories by viewModel.hiddenDefaultCategories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var showHiddenCategories by remember { mutableStateOf(false) }
    
    // Показываем сообщение об ошибке в Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Управление категориями") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            IconButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить категорию")
            }
        }
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Заголовок "Стандартные категории"
                item {
                    Text(
                        text = "Стандартные категории",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                // Стандартные категории
                items(defaultCategories) { category ->
                    CategoryItem(
                        category = category,
                        onDeleteClick = { viewModel.hideDefaultCategory(category.id) },
                        isDeletable = true,
                        deleteIcon = Icons.Default.VisibilityOff,
                        deleteContentDescription = "Скрыть категорию"
                    )
                }
                
                // Заголовок "Скрытые стандартные категории" и кнопка переключения
                if (hiddenDefaultCategories.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Скрытые стандартные категории",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            TextButton(onClick = { showHiddenCategories = !showHiddenCategories }) {
                                Text(if (showHiddenCategories) "Скрыть" else "Показать")
                            }
                        }
                    }
                    
                    // Скрытые стандартные категории (если showHiddenCategories == true)
                    if (showHiddenCategories) {
                        items(hiddenDefaultCategories) { category ->
                            CategoryItem(
                                category = category,
                                onDeleteClick = { viewModel.showDefaultCategory(category.id) },
                                isDeletable = true,
                                deleteIcon = Icons.Default.Visibility,
                                deleteContentDescription = "Показать категорию"
                            )
                        }
                    }
                }
                
                // Заголовок "Ваши категории"
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Ваши категории",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                // Если пользовательских категорий нет - показываем соответствующее сообщение
                if (userCategories.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "У вас пока нет собственных категорий",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    // Пользовательские категории
                    items(userCategories) { category ->
                        CategoryItem(
                            category = category,
                            onDeleteClick = { viewModel.deleteCategory(category.id) },
                            isDeletable = true,
                            deleteIcon = Icons.Default.Delete,
                            deleteContentDescription = "Удалить категорию"
                        )
                    }
                }
                
                // Добавляем отступ внизу для лучшего UX
                item {
                    Spacer(modifier = Modifier.height(80.dp)) // Для FAB и отступа снизу
                }
            }
        }
    }
    
    // Диалог для добавления новой категории
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Новая категория") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Название категории") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addCategory(newCategoryName)
                        newCategoryName = ""
                        showAddDialog = false
                    }
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun CategoryItem(
    category: TaskCategory,
    onDeleteClick: (() -> Unit)? = null, // null если нельзя удалить
    isDeletable: Boolean = false,
    deleteIcon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Delete,
    deleteContentDescription: String = "Удалить"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge
            )
            
            if (onDeleteClick != null && isDeletable) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        deleteIcon,
                        contentDescription = deleteContentDescription,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
} 