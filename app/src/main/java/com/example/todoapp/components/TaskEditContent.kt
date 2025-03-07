package com.example.todoapp.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todoapp.model.TaskCategory
import com.example.todoapp.model.TaskPriority
import com.example.todoapp.utils.showDateTimePickerDialog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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
    buttonText: String
) {
    var showPriorityMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        TextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Название задачи") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        TextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Описание") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 16.dp)
        )

        // Выбор приоритета
        Box {
            OutlinedButton(
                onClick = { showPriorityMenu = true },
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Категория: ${category.name}")
            }
            DropdownMenu(
                expanded = showCategoryMenu,
                onDismissRequest = { showCategoryMenu = false }
            ) {
                TaskCategory.values().forEach { categoryOption ->
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(deadline?.format(dateFormatter) ?: "Установить дедлайн")
        }

        if (deadline != null) {
            TextButton(
                onClick = { onDeadlineChange(null) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Удалить дедлайн")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text(buttonText)
        }
    }
}
