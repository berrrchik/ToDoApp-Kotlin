package com.example.todoapp.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.todoapp.model.Task
import java.time.LocalDateTime

@Composable
fun TaskItem(
    task: Task,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCompleteClick: () -> Unit,
    completeIcon: ImageVector = Icons.Default.CheckCircle,
    completeIconDescription: String = "Завершить"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(),
        onClick = { }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Категория: ${task.category.name} • Приоритет: ${task.priority.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    task.deadline?.let { deadline ->
                        Text(
                            text = "Дедлайн: $deadline",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (deadline.isBefore(LocalDateTime.now()))
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Редактировать",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    IconButton(onClick = onCompleteClick) {
                        Icon(
                            completeIcon,
                            contentDescription = completeIconDescription,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
