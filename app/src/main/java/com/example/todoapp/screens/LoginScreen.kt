package com.example.todoapp.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todoapp.api.ApiClient
import com.example.todoapp.model.AuthRequest
import com.example.todoapp.navigation.Screen
import com.example.todoapp.utils.TokenManager
import com.example.todoapp.viewmodel.CategoryViewModel
import com.example.todoapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(
    navController: NavController,
    taskViewModel: TaskViewModel? = null,
    categoryViewModel: CategoryViewModel? = null
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Проверка авторизации
    LaunchedEffect(Unit) {
        if (tokenManager.isLoggedIn()) {
            Log.d(TAG, "Пользователь уже авторизован, загрузка данных")
            categoryViewModel?.loadCategories()
            taskViewModel?.loadTasks()
            
            navController.navigate(Screen.ActiveTasks.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isLandscape) 32.dp else 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var login by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Text(
            text = "Вход",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        TextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логин") },
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

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = null
                    try {
                        Log.d(TAG, "Попытка входа для пользователя: $login")
                        val response = ApiClient.authApi.login(
                            AuthRequest(login = login, password = password)
                        )
                        if (response.isSuccessful) {
                            val token = response.body()?.token
                            if (token != null) {
                                Log.d(TAG, "Вход успешен, сохранение токена")
                                tokenManager.saveToken(token)
                                
                                // Сначала очищаем кэш категорий
                                Log.d(TAG, "Очистка кэша категорий перед загрузкой")
                                categoryViewModel?.let {
                                    // Используем репозиторий напрямую для очистки кэша
                                    it.clearCategoryCache()
                                }
                                
                                // Загружаем данные перед переходом на главный экран
                                Log.d(TAG, "Загрузка категорий после входа")
                                categoryViewModel?.loadCategories()
                                
                                Log.d(TAG, "Загрузка задач после входа")
                                taskViewModel?.loadTasks()
                                
                                navController.navigate(Screen.ActiveTasks.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            } else {
                                errorMessage = "Ошибка авторизации"
                                Log.e(TAG, "Ошибка авторизации: пустой токен")
                            }
                        } else {
                            errorMessage = when (response.code()) {
                                400 -> "Неверный логин или пароль"
                                else -> "Ошибка сервера: ${response.code()}"
                            }
                            Log.e(TAG, "Ошибка авторизации: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        errorMessage = "Ошибка соединения: ${e.message}"
                        Log.e(TAG, "Ошибка соединения при входе", e)
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.surface
                )
            } else {
                Text("Войти")
            }
        }

        TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
            Text("Регистрация")
        }
    }
}
