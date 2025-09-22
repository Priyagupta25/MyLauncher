package com.example.myapplication.ui.compose

import HomeGrid
import LauncherOverview
import ViewPagerShortchuts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height


import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.Utils.toDomain
import com.example.myapplication.data.local.entity.LauncherItem
import com.example.myapplication.ui.main.LauncherViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(viewModel: LauncherViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(
        initialPage = 1,             // 👈 Default page (index 1 = 2nd page)
        pageCount = { 2 }   // Total number of pages
    )
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { pageIndex ->
        if (pageIndex ==0){
            val context = LocalContext.current
            val homeShortcuts by viewModel.shortcut.collectAsState()
            val apps = viewModel.getAllInstalledApps(context)
            var shorcuts by remember { mutableStateOf(emptyList<LauncherItem>()) }
            LaunchedEffect(homeShortcuts) {

                shorcuts =  homeShortcuts.map { shortcut ->
                    shortcut.apps.forEach() { app ->
                        val index = apps.indexOfFirst { it.packageName == app.packageName }
                        if (index == -1)
                            shortcut.apps.remove(app)
                        else
                            app.icon = apps.get(index).icon
                    }
                    shortcut.toDomain()
                }
            }
            HomeGrid(viewModel = viewModel,shorcuts)
        }else{
            LauncherOverview(viewModel)
        }
    }
}