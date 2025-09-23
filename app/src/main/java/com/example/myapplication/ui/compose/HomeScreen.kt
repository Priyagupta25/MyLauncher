package com.example.myapplication.ui.compose


import HomeGrid
import LauncherOverview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.Utils.toDomain
import com.example.myapplication.ui.main.LauncherViewModel
import kotlinx.coroutines.flow.map

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

            val apps by viewModel.installedApps
                .map { it.sortedBy { app -> app.packageName } } // normalize order
                .collectAsStateWithLifecycle(emptyList())
            if (!apps.isEmpty()) {
                val homeShortcuts by viewModel.shortcut.collectAsState()

                // ⚡ Compute shortcuts immutably
                val shortcuts by remember(homeShortcuts, apps) {
                    derivedStateOf {
                        homeShortcuts.map { shortcut ->
                            shortcut.copy(
                                apps = shortcut.apps.mapNotNull { app ->
                                    apps.find { it.packageName == app.packageName }?.let {
                                        app.copy(icon = it.icon)
                                    }
                                }
                            ).toDomain()
                        }
                    }
                }
                // ⚡ Handle deletion as a side effect
                LaunchedEffect(homeShortcuts, apps) {
                    homeShortcuts.forEach { shortcut ->
                        if (shortcut.apps.none { app -> apps.any { it.packageName == app.packageName } }) {
                            viewModel.deleteItem(shortcut)
                        }
                    }
                }

                HomeGrid(viewModel = viewModel, shortcuts)
            }
        }else{
            LauncherOverview(viewModel)
        }
    }
}