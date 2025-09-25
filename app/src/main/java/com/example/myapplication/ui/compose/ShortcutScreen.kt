package com.example.myapplication.ui.compose

import FolderIcon
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.myapplication.data.local.entity.AppInfo
import com.example.myapplication.data.local.entity.LauncherItem
import com.example.myapplication.data.local.util.Utils
import com.example.myapplication.data.local.util.Utils.toDomain
import com.example.myapplication.ui.main.LauncherViewModel

@Composable
fun ShortcutScreen(viewModel: LauncherViewModel, apps: List<AppInfo>, onSwipeUp: () -> Unit) {

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -20) onSwipeUp()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        ViewPagerShortchuts(shortcuts, viewModel)
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomBarApps()
            MySearchBar(onQuery = { })
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun BottomBarApps() {
    val context = LocalContext.current

    val dialerPackage = remember(context) { Utils.getDialerAppPackageName(context) }
    val cameraPackage = remember(context) { Utils.getCameraAppPackageName(context) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        dialerPackage?.let { DockAppIcon(it, "Phone") }
        DockAppIcon("com.whatsapp", "Messages")
        cameraPackage?.let { DockAppIcon(it, "Camera") }
        DockAppIcon("com.android.chrome", "Browser")
    }

}

@Composable
fun ViewPagerShortchuts(
    homeShortcuts: List<LauncherItem>,
    viewModel: LauncherViewModel,
) {
    val pageSize = 3
    val pages = homeShortcuts.chunked(pageSize)
    val pagerState = rememberPagerState { pages.size }
    Column(
        modifier = Modifier
            .fillMaxHeight(0.6f)
            .fillMaxWidth()

    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            HomeGrid(viewModel = viewModel, pages.get(page))
        }

        // Pager indicator
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            repeat(pages.size) { index ->
                val height = if (pagerState.currentPage == index) 15.dp else 10.dp
                val width = if (pagerState.currentPage == index) 15.dp else 10.dp
                Box(
                    modifier = Modifier
                        .height(height)
                        .width(width)
                        .padding(2.dp)
                        .background(
                            if (pagerState.currentPage == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}

@Composable
fun HomeGrid(viewModel: LauncherViewModel, gridItem: List<LauncherItem>) {

    val iconBounds = remember { mutableStateMapOf<String, Rect>() }

    LazyVerticalGrid(columns = GridCells.Fixed(4)) {
        // Pass the actual list instead of count
        items(
            items = gridItem,
            key = { it.id } // stable key for each item
        ) { item ->
            when (item) {
                is LauncherItem.App -> DraggableAppIcon(
                    app = item,
                    viewModel = viewModel,
                    iconBounds = iconBounds,
                    onDrop = { draggedApp, dropOffset ->
                        if (draggedApp !is LauncherItem.App) return@DraggableAppIcon

                        val rect =
                            iconBounds[draggedApp.appInfo.packageName] ?: return@DraggableAppIcon
                        val dropX = dropOffset.x + rect.left
                        val dropY = dropOffset.y + rect.top

                        Log.d("Priya", "rect $rect")
                        Log.d("Priya", "dropX $dropX")
                        Log.d("Priya", "dropY $dropY")
                        Log.d("Priya", "iconBounds $iconBounds")

                        val target = iconBounds.entries.find { (id, r) ->
                            id != draggedApp.appInfo.packageName &&
                                    dropX in r.left..r.right &&
                                    dropY in r.top..r.bottom
                        }

                        val targetItem = gridItem.firstOrNull { it.id == target?.key }
                        if (targetItem != null) {
                            viewModel.handleDrop(draggedApp, targetItem)
                        }
                    }
                )

                is LauncherItem.Folder -> FolderIcon(item, iconBounds)
            }
        }
    }
}


@Composable
fun DockAppIcon(packageName: String, fallbackLabel: String) {
    val context = LocalContext.current
    val pm = context.packageManager
    val launchIntent = pm.getLaunchIntentForPackage(packageName)
    val appInfo = remember {
        try {
            pm.getApplicationInfo(packageName, 0)
        } catch (e: Exception) {
            null
        }
    }
    val label = appInfo?.loadLabel(pm)?.toString() ?: fallbackLabel
    val icon = appInfo?.loadIcon(pm)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            launchIntent?.let { context.startActivity(it) }
        }
    ) {
        if (icon != null) {
            Image(
                bitmap = icon.toBitmap().asImageBitmap(),
                contentDescription = label,
                modifier = Modifier.size(48.dp)
            )
        }

    }
}
