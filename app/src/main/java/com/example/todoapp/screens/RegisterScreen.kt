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
import com.example.todoapp.model.RegisterRequest
import com.example.todoapp.navigation.Screen
import com.example.todoapp.utils.TokenManager
import com.example.todoapp.viewmodel.CategoryViewModel
import com.example.todoapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

private const val TAG = "RegisterScreen"

@Composable
fun RegisterScreen(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isLandscape) 32.dp else 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var login by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        Text(
            text = "Регистрация",
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

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (password != confirmPassword) {
                    errorMessage = "Пароли не совпадают"
                    return@Button
                }
                
                scope.launch {
                    isLoading = true
                    errorMessage = null
                    try {
                        Log.d(TAG, "Попытка регистрации пользователя: $login")
                        val response = ApiClient.authApi.register(
                            RegisterRequest(
                                login = login,
                                email = email,
                                password = password
                            )
                        )
                        if (response.isSuccessful) {
                            val token = response.body()?.token
                            if (token != null) {
                                Log.d(TAG, "Регистрация успешна, сохранение токена")
                                tokenManager.saveToken(token)
                                
                                // Очищаем кэш категорий перед загрузкой
                                Log.d(TAG, "Очистка кэша категорий перед загрузкой")
                                categoryViewModel?.let {
                                    it.clearCategoryCache()
                                }
                                
                                // Загружаем данные перед переходом на главный экран
                                Log.d(TAG, "Создание стандартных категорий для нового пользователя")
                                categoryViewModel?.loadCategories()
                                
                                Log.d(TAG, "Загрузка задач после регистрации")
                                taskViewModel?.loadTasks()
                                
                                navController.navigate(Screen.ActiveTasks.route) {
                                    popUpTo(Screen.Register.route) { inclusive = true }
                                }
                            } else {
                                errorMessage = "Ошибка регистрации"
                                Log.e(TAG, "Ошибка регистрации: пустой токен")
                            }
                        } else {
                            errorMessage = when (response.code()) {
                                400 -> "Неверный формат email"
                                409 -> "Пользователь уже существует"
                                else -> "Ошибка сервера: ${response.code()}"
                            }
                            Log.e(TAG, "Ошибка регистрации: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        errorMessage = "Ошибка соединения: ${e.message}"
                        Log.e(TAG, "Ошибка соединения при регистрации", e)
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
                Text("Зарегистрироваться")
            }
        }

        TextButton(onClick = { navController.navigateUp() }) {
            Text("Назад")
        }
    }
}
