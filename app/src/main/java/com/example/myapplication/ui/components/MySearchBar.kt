package com.example.myapplication.ui.components

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Search


import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun MySearchBar(
    onQuery: (TextFieldValue) -> Unit,
) {
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    OutlinedTextField(
        value = textState,
        onValueChange = { textState = it
            onQuery(it) },
        placeholder = { Text("Search apps…", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth(0.9f)

            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large
            ), // white curved bg
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground), // black text
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        shape = MaterialTheme.shapes.large,
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        }
    )
}


