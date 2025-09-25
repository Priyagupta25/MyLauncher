package com.example.myapplication.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.local.entity.AppInfo
import com.example.myapplication.ui.main.LauncherViewModel


@Composable
fun AppDrawer(
    apps: List<AppInfo>, viewModel: LauncherViewModel,
    onQuery: (TextFieldValue) -> Unit, onLaunch: (AppInfo) -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MySearchBar(onQuery = onQuery)
        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            contentPadding = PaddingValues(8.dp),
            columns = GridCells.Adaptive(minSize = 80.dp),
            modifier = Modifier
                .fillMaxHeight(1f) // Take 80% of screen
                .padding(8.dp)
        ) {
            items(apps, key = { item -> item.packageName }) { item ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onLaunch(item) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppItem(item, viewModel)
                }
            }
        }
    }
}