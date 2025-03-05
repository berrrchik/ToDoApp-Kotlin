//package com.example.todoapp
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.example.todoapp.navigation.Screen
//import com.example.todoapp.ui.theme.ToDoAppTheme
//import androidx.compose.animation.*
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Edit
//import androidx.navigation.NavType
//import java.time.LocalDateTime
//import com.example.todoapp.model.*
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.ui.graphics.vector.ImageVector
//import java.time.LocalTime
//import com.example.todoapp.model.TaskPriority
//import com.example.todoapp.model.TaskCategory
//import android.app.DatePickerDialog
//import android.app.TimePickerDialog
//import androidx.compose.runtime.remember
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//import android.content.Context
//import androidx.compose.ui.platform.LocalContext
//import com.example.todoapp.components.SearchBar
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.example.todoapp.viewmodel.TaskViewModel
//import com.example.todoapp.api.TaskApiService
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            ToDoAppTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    TodoApp()
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TodoApp(viewModel: TaskViewModel = viewModel()) {
//    val navController = rememberNavController()
//    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
//    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
//
//    NavHost(
//        navController = navController,
//        startDestination = Screen.Login.route,
//        enterTransition = { slideInHorizontally() },
//        exitTransition = { slideOutHorizontally() }
//    ) {
//        composable(Screen.Login.route) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                var email by remember { mutableStateOf("") }
//                var password by remember { mutableStateOf("") }
//
//                Text(
//                    text = "Вход",
//                    style = MaterialTheme.typography.headlineMedium,
//                    modifier = Modifier.padding(bottom = 32.dp)
//                )
//
//                TextField(
//                    value = email,
//                    onValueChange = { email = it },
//                    label = { Text("Email") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp)
//                )
//
//                TextField(
//                    value = password,
//                    onValueChange = { password = it },
//                    label = { Text("Пароль") },
//                    visualTransformation = PasswordVisualTransformation(),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp)
//                )
//
//                Button(
//                    onClick = { navController.navigate(Screen.ActiveTasks.route) },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp)
//                ) {
//                    Text("Войти")
//                }
//
//                TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
//                    Text("Регистрация")
//                }
//            }
//        }
//
//        composable(Screen.Register.route) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                var email by remember { mutableStateOf("") }
//                var password by remember { mutableStateOf("") }
//                var confirmPassword by remember { mutableStateOf("") }
//
//                Text(
//                    text = "Регистрация",
//                    style = MaterialTheme.typography.headlineMedium,
//                    modifier = Modifier.padding(bottom = 32.dp)
//                )
//
//                TextField(
//                    value = email,
//                    onValueChange = { email = it },
//                    label = { Text("Email") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp)
//                )
//
//                TextField(
//                    value = password,
//                    onValueChange = { password = it },
//                    label = { Text("Пароль") },
//                    visualTransformation = PasswordVisualTransformation(),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp)
//                )
//
//                TextField(
//                    value = confirmPassword,
//                    onValueChange = { confirmPassword = it },
//                    label = { Text("Подтвердите пароль") },
//                    visualTransformation = PasswordVisualTransformation(),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp)
//                )
//
//                Button(
//                    onClick = { navController.navigate(Screen.Login.route) },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp)
//                ) {
//                    Text("Зарегистрироваться")
//                }
//
//                TextButton(onClick = { navController.navigateUp() }) {
//                    Text("Назад")
//                }
//            }
//        }
//
//        composable(Screen.ActiveTasks.route) {
//            Scaffold(
//                topBar = {
//                    Column {
//                        TopAppBar(
//                            title = { Text("Активные задачи") },
//                            colors = TopAppBarDefaults.topAppBarColors(
//                                containerColor = MaterialTheme.colorScheme.primaryContainer,
//                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                            ),
//                            actions = {
//                                IconButton(onClick = { navController.navigate(Screen.DeletedTasks.route) }) {
//                                    Icon(Icons.Default.Delete, contentDescription = "Удаленные задачи")
//                                }
//                                IconButton(onClick = { navController.navigate(Screen.CompletedTasks.route) }) {
//                                    Icon(Icons.Default.CheckCircle, contentDescription = "Выполненные задачи")
//                                }
//                            }
//                        )
//                        SearchBar(
//                            searchQuery = searchQuery,
//                            onSearchQueryChange = { viewModel.updateSearchQuery(it) }
//                        )
//                    }
//                },
//                floatingActionButton = {
//                    FloatingActionButton(
//                        onClick = { navController.navigate(Screen.AddTask.route) },
//                        containerColor = MaterialTheme.colorScheme.primaryContainer
//                    ) {
//                        Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
//                    }
//                }
//            ) { padding ->
//                if (tasks.isEmpty()) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(padding),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "Нет активных задач",
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                        )
//                    }
//                } else {
//                    val filteredTasks = tasks.filterNot { it.isCompleted || it.isDeleted }
//                        .filter {
//                            searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true) ||
//                                    it.description.contains(searchQuery, ignoreCase = true)
//                        }
//
//                    if (filteredTasks.isEmpty() && searchQuery.isNotEmpty()) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(padding),
//                            contentAlignment = Alignment.Center
//                        ) {
//    Text(
//                                text = "Ничего не найдено",
//                                style = MaterialTheme.typography.bodyLarge,
//                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                            )
//                        }
//                    } else {
//                        LazyColumn(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(padding)
//                        ) {
//                            items(filteredTasks.size) { index ->
//                                val task = filteredTasks[index]
//                                TaskItem(
//                                    task = task,
//                                    onEditClick = {
//                                        navController.navigate(Screen.EditTask.createRoute(task.id))
//                                    },
//                                    onDeleteClick = {
//                                        viewModel.updateTasks(
//                                            tasks.map {
//                                                if (it.id == task.id) it.copy(isDeleted = true) else it
//                                            }
//                                        )
//                                    },
//                                    onCompleteClick = {
//                                        viewModel.updateTasks(
//                                            tasks.map {
//                                                if (it.id == task.id) it.copy(isCompleted = true) else it
//                                            }
//                                        )
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        composable(Screen.CompletedTasks.route) {
//            Scaffold(
//                topBar = {
//                    TopAppBar(
//                        title = { Text("Выполненные задачи") },
//                        navigationIcon = {
//                            IconButton(onClick = { navController.navigateUp() }) {
//                                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
//                            }
//                        }
//                    )
//                }
//            ) { padding ->
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(padding)
//                ) {
//                    items(tasks.filter { it.isCompleted && !it.isDeleted }.size) { index ->
//                        val task = tasks.filter { it.isCompleted && !it.isDeleted }[index]
//                        TaskItem(
//                            task = task,
//                            onEditClick = {
//                                navController.navigate(Screen.EditTask.createRoute(task.id))
//                            },
//                            onDeleteClick = {
//                                viewModel.updateTasks(
//                                    tasks.map {
//                                        if (it.id == task.id) it.copy(isDeleted = true) else it
//                                    }
//                                )
//                            },
//                            onCompleteClick = {
//                                viewModel.updateTasks(
//                                    tasks.map {
//                                        if (it.id == task.id) it.copy(isCompleted = false) else it
//                                    }
//                                )
//                            }
//                        )
//                    }
//                }
//            }
//        }
//
//        composable(Screen.DeletedTasks.route) {
//            Scaffold(
//                topBar = {
//                    TopAppBar(
//                        title = { Text("Удаленные задачи") },
//                        navigationIcon = {
//                            IconButton(onClick = { navController.navigateUp() }) {
//                                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
//                            }
//                        }
//                    )
//                }
//            ) { padding ->
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(padding)
//                ) {
//                    items(tasks.filter { it.isDeleted }.size) { index ->
//                        val task = tasks.filter { it.isDeleted }[index]
//                        TaskItem(
//                            task = task,
//                            onEditClick = { },
//                            onDeleteClick = {
//                                viewModel.updateTasks(
//                                    tasks.filterNot { it.id == task.id }
//                                )
//                            },
//                            onCompleteClick = {
//                                viewModel.updateTasks(
//                                    tasks.map {
//                                        if (it.id == task.id) it.copy(isDeleted = false) else it
//                                    }
//                                )
//                            },
//                            completeIcon = Icons.Default.Restore,
//                            completeIconDescription = "Восстановить"
//                        )
//                    }
//                }
//            }
//        }
//
//        composable(
//            route = Screen.EditTask.route,
//            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
//            val task = tasks.find { it.id == taskId } ?: return@composable
//
//            var title by remember { mutableStateOf(task.title) }
//            var description by remember { mutableStateOf(task.description) }
//            var priority by remember { mutableStateOf(task.priority) }
//            var category by remember { mutableStateOf(task.category) }
//            var deadline by remember { mutableStateOf(task.deadline) }
//
//            Scaffold(
//                topBar = {
//                    TopAppBar(
//                        title = { Text("Редактировать задачу") },
//                        navigationIcon = {
//                            IconButton(onClick = { navController.navigateUp() }) {
//                                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
//                            }
//                        }
//                    )
//                }
//            ) { padding ->
//                TaskEditContent(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(padding),
//                    title = title,
//                    onTitleChange = { title = it },
//                    description = description,
//                    onDescriptionChange = { description = it },
//                    priority = priority,
//                    onPriorityChange = { priority = it },
//                    category = category,
//                    onCategoryChange = { category = it },
//                    deadline = deadline,
//                    onDeadlineChange = { deadline = it },
//                    onSave = {
//                        viewModel.updateTasks(
//                            tasks.map {
//                                if (it.id == taskId) {
//                                    it.copy(
//                                        title = title,
//                                        description = description,
//                                        priority = priority,
//                                        category = category,
//                                        deadline = deadline
//                                    )
//                                } else it
//                            }
//                        )
//                        navController.navigateUp()
//                    },
//                    buttonText = "Сохранить изменения"
//                )
//            }
//        }
//
//        composable(Screen.AddTask.route) {
//            var title by remember { mutableStateOf("") }
//            var description by remember { mutableStateOf("") }
//            var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
//            var category by remember { mutableStateOf(TaskCategory.OTHER) }
//            var deadline by remember { mutableStateOf<LocalDateTime?>(null) }
//
//            Scaffold(
//                topBar = {
//                    TopAppBar(
//                        title = { Text("Новая задача") },
//                        navigationIcon = {
//                            IconButton(onClick = { navController.navigateUp() }) {
//                                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
//                            }
//                        }
//                    )
//                }
//            ) { padding ->
//                TaskEditContent(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(padding),
//                    title = title,
//                    onTitleChange = { title = it },
//                    description = description,
//                    onDescriptionChange = { description = it },
//                    priority = priority,
//                    onPriorityChange = { priority = it },
//                    category = category,
//                    onCategoryChange = { category = it },
//                    deadline = deadline,
//                    onDeadlineChange = { deadline = it },
//                    onSave = {
//                        if (title.isNotBlank()) {
//                            val newTask = Task(
//                                id = tasks.size,
//                                title = title,
//                                description = description,
//                                priority = priority,
//                                category = category,
//                                deadline = deadline,
//                                isCompleted = false
//                            )
//                            viewModel.updateTasks(tasks + newTask)
//                            navController.navigateUp()
//                        }
//                    },
//                    buttonText = "Создать задачу"
//                )
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TaskItem(
//    task: Task,
//    onEditClick: () -> Unit,
//    onDeleteClick: () -> Unit,
//    onCompleteClick: () -> Unit,
//    completeIcon: ImageVector = Icons.Default.CheckCircle,
//    completeIconDescription: String = "Завершить"
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .animateContentSize(),
//        onClick = { }
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth()
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(modifier = Modifier.weight(1f)) {
//                    Text(
//                        text = task.title,
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                    Text(
//                        text = "Категория: ${task.category.name} • Приоритет: ${task.priority.name}",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                    task.deadline?.let { deadline ->
//                        Text(
//                            text = "Дедлайн: $deadline",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = if (deadline.isBefore(LocalDateTime.now()))
//                                MaterialTheme.colorScheme.error
//                            else
//                                MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//                }
//                Row {
//                    IconButton(onClick = onEditClick) {
//                        Icon(
//                            Icons.Default.Edit,
//                            contentDescription = "Редактировать",
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                    IconButton(onClick = onDeleteClick) {
//                        Icon(
//                            Icons.Default.Delete,
//                            contentDescription = "Удалить",
//                            tint = MaterialTheme.colorScheme.error
//                        )
//                    }
//                    IconButton(onClick = onCompleteClick) {
//                        Icon(
//                            completeIcon,
//                            contentDescription = completeIconDescription,
//                            tint = MaterialTheme.colorScheme.secondary
//                        )
//                    }
//                }
//            }
//            if (task.description.isNotBlank()) {
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = task.description,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun TaskEditContent(
//    modifier: Modifier = Modifier,
//    title: String,
//    onTitleChange: (String) -> Unit,
//    description: String,
//    onDescriptionChange: (String) -> Unit,
//    priority: TaskPriority,
//    onPriorityChange: (TaskPriority) -> Unit,
//    category: TaskCategory,
//    onCategoryChange: (TaskCategory) -> Unit,
//    deadline: LocalDateTime?,
//    onDeadlineChange: (LocalDateTime?) -> Unit,
//    onSave: () -> Unit,
//    buttonText: String
//) {
//    var showPriorityMenu by remember { mutableStateOf(false) }
//    var showCategoryMenu by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") }
//
//    Column(
//        modifier = modifier.padding(16.dp)
//    ) {
//        TextField(
//            value = title,
//            onValueChange = onTitleChange,
//            label = { Text("Название задачи") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp)
//        )
//
//        TextField(
//            value = description,
//            onValueChange = onDescriptionChange,
//            label = { Text("Описание") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp)
//                .padding(bottom = 16.dp)
//        )
//
//        // Выбор приоритета
//        Box {
//            OutlinedButton(
//                onClick = { showPriorityMenu = true },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Приоритет: ${priority.name}")
//            }
//            DropdownMenu(
//                expanded = showPriorityMenu,
//                onDismissRequest = { showPriorityMenu = false }
//            ) {
//                TaskPriority.values().forEach { priorityOption ->
//                    DropdownMenuItem(
//                        text = { Text(priorityOption.name) },
//                        onClick = {
//                            onPriorityChange(priorityOption)
//                            showPriorityMenu = false
//                        }
//                    )
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Выбор категории
//        Box {
//            OutlinedButton(
//                onClick = { showCategoryMenu = true },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Категория: ${category.name}")
//            }
//            DropdownMenu(
//                expanded = showCategoryMenu,
//                onDismissRequest = { showCategoryMenu = false }
//            ) {
//                TaskCategory.values().forEach { categoryOption ->
//                    DropdownMenuItem(
//                        text = { Text(categoryOption.name) },
//                        onClick = {
//                            onCategoryChange(categoryOption)
//                            showCategoryMenu = false
//                        }
//                    )
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Выбор дедлайна
//        OutlinedButton(
//            onClick = {
//                showDateTimePickerDialog(context) { selectedDateTime ->
//                    onDeadlineChange(selectedDateTime)
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(deadline?.format(dateFormatter) ?: "Установить дедлайн")
//        }
//
//        if (deadline != null) {
//            TextButton(
//                onClick = { onDeadlineChange(null) },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Удалить дедлайн")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = onSave,
//            modifier = Modifier.fillMaxWidth(),
//            enabled = title.isNotBlank()
//        ) {
//            Text(buttonText)
//        }
//    }
//}
//
//private fun showDateTimePickerDialog(
//    context: Context,
//    onDateTimeSelected: (LocalDateTime) -> Unit
//) {
//    val currentDate = LocalDate.now()
//    val currentTime = LocalTime.now()
//
//    DatePickerDialog(
//        context,
//        { _, year, month, dayOfMonth ->
//            TimePickerDialog(
//                context,
//                { _, hourOfDay, minute ->
//                    val selectedDateTime = LocalDateTime.of(
//                        year, month + 1, dayOfMonth,
//                        hourOfDay, minute
//                    )
//                    onDateTimeSelected(selectedDateTime)
//                },
//                currentTime.hour,
//                currentTime.minute,
//                true
//            ).show()
//        },
//        currentDate.year,
//        currentDate.monthValue - 1,
//        currentDate.dayOfMonth
//    ).show()
//}

package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoapp.navigation.Screen
import com.example.todoapp.ui.theme.ToDoAppTheme
import androidx.compose.animation.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.navigation.NavType
import java.time.LocalDateTime
import com.example.todoapp.model.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalTime
import com.example.todoapp.model.TaskPriority
import com.example.todoapp.model.TaskCategory
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.runtime.remember
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.todoapp.components.SearchBar
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.viewmodel.TaskViewModel
import com.example.todoapp.api.TaskApiService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TodoApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(viewModel: TaskViewModel = viewModel()) {
    val navController = rememberNavController()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        enterTransition = { slideInHorizontally() },
        exitTransition = { slideOutHorizontally() }
    ) {
        composable(Screen.Login.route) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                Text(
                    text = "Вход",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Button(
                    onClick = { navController.navigate(Screen.ActiveTasks.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Войти")
                }

                TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                    Text("Регистрация")
                }
            }
        }

        composable(Screen.Register.route) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }

                Text(
                    text = "Регистрация",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Подтвердите пароль") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Button(
                    onClick = { navController.navigate(Screen.Login.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Зарегистрироваться")
                }

                TextButton(onClick = { navController.navigateUp() }) {
                    Text("Назад")
                }
            }
        }

        composable(Screen.ActiveTasks.route) {
            Scaffold(
                topBar = {
                    Column {
                        TopAppBar(
                            title = { Text("Активные задачи") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            actions = {
                                IconButton(onClick = { navController.navigate(Screen.DeletedTasks.route) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Удаленные задачи")
                                }
                                IconButton(onClick = { navController.navigate(Screen.CompletedTasks.route) }) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Выполненные задачи")
                                }
                            }
                        )
                        SearchBar(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.updateSearchQuery(it) }
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.AddTask.route) },
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
                    }
                }
            ) { padding ->
                if (tasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет активных задач",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    val filteredTasks = tasks.filterNot { it.isCompleted || it.isDeleted }
                        .filter {
                            searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true) ||
                                    it.description.contains(searchQuery, ignoreCase = true)
                        }

                    if (filteredTasks.isEmpty() && searchQuery.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ничего не найдено",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        ) {
                            items(filteredTasks.size) { index ->
                                val task = filteredTasks[index]
                                TaskItem(
                                    task = task,
                                    onEditClick = {
                                        navController.navigate(Screen.EditTask.createRoute(task.id))
                                    },
                                    onDeleteClick = {

//                                        viewModel.updateTasks(
//                                            tasks.map {
//                                                if (it.id == task.id) it.copy(isDeleted = true) else it
//                                            }
//                                        )

//                                        viewModel.deleteTask(task.id)
                                        viewModel.updateTask(
                                            task.copy(isDeleted = true)
                                        )
                                    },
                                    onCompleteClick = {

//                                        viewModel.updateTasks(
//                                            tasks.map {
//                                                if (it.id == task.id) it.copy(isCompleted = true) else it
//                                            }

                                        viewModel.updateTask(
                                            task.copy(isCompleted = true)
                                        )

                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        composable(Screen.CompletedTasks.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Выполненные задачи") },
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
                    items(tasks.filter { it.isCompleted && !it.isDeleted }.size) { index ->
                        val task = tasks.filter { it.isCompleted && !it.isDeleted }[index]
                        TaskItem(
                            task = task,
                            onEditClick = {
                                navController.navigate(Screen.EditTask.createRoute(task.id))
                            },
                            onDeleteClick = {

//                                viewModel.updateTasks(
//                                    tasks.map {
//                                        if (it.id == task.id) it.copy(isDeleted = true) else it
//                                    }
//                                )
                                viewModel.deleteTask(task.id)
                            },
                            onCompleteClick = {

//                                viewModel.updateTasks(
//                                    tasks.map {
//                                        if (it.id == task.id) it.copy(isCompleted = false) else it
//                                    }
                                viewModel.updateTask(
                                    task.copy(isCompleted = false)
                                )
                            }
                        )
                    }
                }
            }
        }

        composable(Screen.DeletedTasks.route) {
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
//                                viewModel.updateTasks(
//                                    tasks.filterNot { it.id == task.id }
//                                )
                                viewModel.deleteTask(task.id)
                            },
                            onCompleteClick = {
//                                viewModel.updateTasks(
//                                    tasks.map {
//                                        if (it.id == task.id) it.copy(isDeleted = false) else it
//                                    }
                                viewModel.updateTask(
                                    task.copy(isDeleted = false)
                                )
                            },
                            completeIcon = Icons.Default.Restore,
                            completeIconDescription = "Восстановить"
                        )
                    }
                }
            }
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
            val task = tasks.find { it.id == taskId } ?: return@composable

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
                    onSave = {
//                        viewModel.updateTasks(
//                            tasks.map {
//                                if (it.id == taskId) {
//                                    it.copy(
//                                        title = title,
//                                        description = description,
//                                        priority = priority,
//                                        category = category,
//                                        deadline = deadline
//                                    )
//                                } else it
//                            }
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
                    buttonText = "Сохранить изменения"
                )
            }
        }

        composable(Screen.AddTask.route) {
            var title by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }
            var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
            var category by remember { mutableStateOf(TaskCategory.OTHER) }
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
//                                id = tasks.size,
                                id = 0,
                                title = title,
                                description = description,
                                priority = priority,
                                category = category,
                                deadline = deadline,
                                isCompleted = false
                            )
//                            viewModel.updateTasks(tasks + newTask)
                            viewModel.createTask(newTask)
                            navController.navigateUp()
                        }
                    },
                    buttonText = "Создать задачу"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

private fun showDateTimePickerDialog(
    context: Context,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val selectedDateTime = LocalDateTime.of(
                        year, month + 1, dayOfMonth,
                        hourOfDay, minute
                    )
                    onDateTimeSelected(selectedDateTime)
                },
                currentTime.hour,
                currentTime.minute,
                true
            ).show()
        },
        currentDate.year,
        currentDate.monthValue - 1,
        currentDate.dayOfMonth
    ).show()
}