//package com.example.todoapp.components
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Clear
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.FocusRequester
//import androidx.compose.ui.focus.focusRequester
//import androidx.compose.ui.platform.LocalFocusManager
//import androidx.compose.ui.platform.LocalSoftwareKeyboardController
//import androidx.compose.ui.unit.dp
//import android.content.res.Configuration
//import androidx.compose.ui.platform.LocalConfiguration
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SearchBar(
//    searchQuery: String,
//    onSearchQueryChange: (String) -> Unit,
//    onRefresh: () -> Unit,
//    isTasksEmpty: Boolean,
//    modifier: Modifier = Modifier,
//    placeholder: String = "Поиск задач"
//) {
//    val focusRequester = remember { FocusRequester() }
//    val focusManager = LocalFocusManager.current
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val configuration = LocalConfiguration.current
//    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = if (isLandscape) 16.dp else 8.dp)
//    ) {
//        TextField(
//            value = searchQuery,
//            onValueChange = onSearchQueryChange,
//            modifier = Modifier.fillMaxWidth(),
//            placeholder = { Text(placeholder) },
//            singleLine = true,
//            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
//            trailingIcon = {
//                if (searchQuery.isNotEmpty()) {
//                    IconButton(onClick = { onSearchQueryChange("") }) {
//                        Icon(Icons.Default.Clear, contentDescription = "Очистить")
//                    }
//                }
//            },
//            colors = TextFieldDefaults.textFieldColors(
//                containerColor = MaterialTheme.colorScheme.surface
//            )
//        )
//
//        if (isTasksEmpty) {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text("Нет активных задач")
//                    Button(onClick = onRefresh) {
//                        Text("Обновить")
//                    }
//                }
//            }
//        }
//    }
//}

package com.example.todoapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    isTasksEmpty: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String = "Поиск задач"
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .focusRequester(focusRequester),
            placeholder = { Text(placeholder) },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Поиск"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onSearchQueryChange("")
                            keyboardController?.hide()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Очистить"
                        )
                    }
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        if (isTasksEmpty) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Нет активных задач")
                    Button(onClick = onRefresh) {
                        Text("Обновить")
                    }
                }
            }
        }
    }
}