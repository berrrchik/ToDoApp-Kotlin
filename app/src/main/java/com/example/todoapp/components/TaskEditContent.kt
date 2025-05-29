package com.example.todoapp.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.model.TaskPriority
import com.example.todoapp.utils.showDateTimePickerDialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TaskEditContent(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    priority: TaskPriority,
    onPriorityChange: (TaskPriority) -> Unit,
    category: TaskCategory,
    onCategoryChange: (TaskCategory) -> Unit,
    deadline: LocalDateTime?,
    onDeadlineChange: (LocalDateTime?) -> Unit,
    onSave: () -> Unit,
    buttonText: String,
    categories: List<TaskCategory> = TaskCategory.DEFAULT_CATEGORIES,
    isLoading: Boolean = false
) {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    
    var showPriorityMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Название задачи") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            enabled = !isLoading
        )
        
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Описание") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 16.dp),
            enabled = !isLoading
        )

        // Выбор приоритета
        Box {
            OutlinedButton(
                onClick = { showPriorityMenu = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Приоритет: ${priority.name}")
            }
            DropdownMenu(
                expanded = showPriorityMenu,
                onDismissRequest = { showPriorityMenu = false }
            ) {
                TaskPriority.values().forEach { priorityOption ->
                    DropdownMenuItem(
                        text = { Text(priorityOption.name) },
                        onClick = {
                            onPriorityChange(priorityOption)
                            showPriorityMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Выбор категории
        Box {
            OutlinedButton(
                onClick = { showCategoryMenu = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Категория: ${category.name}")
            }
            DropdownMenu(
                expanded = showCategoryMenu,
                onDismissRequest = { showCategoryMenu = false }
            ) {
                categories.forEach { categoryOption ->
                    DropdownMenuItem(
                        text = { Text(categoryOption.name) },
                        onClick = {
                            onCategoryChange(categoryOption)
                            showCategoryMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Выбор дедлайна
        OutlinedButton(
            onClick = {
                showDateTimePickerDialog(context) { selectedDateTime ->
                    onDeadlineChange(selectedDateTime)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(deadline?.format(dateFormatter) ?: "Установить дедлайн")
        }

        if (deadline != null) {
            TextButton(
                onClick = { onDeadlineChange(null) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Удалить дедлайн")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.surface
                )
            } else {
                Text(buttonText)
            }
        }
    }
}
