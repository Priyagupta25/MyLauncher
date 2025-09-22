import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import android.util.Log.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.ui.compose.AppDrawer
import com.example.myapplication.data.local.entity.LauncherItem
import com.example.myapplication.ui.main.LauncherViewModel
import com.example.myapplication.ui.compose.MySearchBar
import com.example.myapplication.Utils
import com.example.myapplication.Utils.toDomain

import com.example.myapplication.ui.compose.DraggableAppIcon
import com.example.myapplication.ui.compose.FolderIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherOverview(viewModel: LauncherViewModel= hiltViewModel()) {
    val context = LocalContext.current
    val apps = viewModel.getAllInstalledApps(context)
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val filteredApps = remember(searchQuery, apps) {
        if (searchQuery.text.isBlank()) apps
        else apps.filter { it.label.startsWith(searchQuery.text, ignoreCase = true) }
    }
    val scope = rememberCoroutineScope()

    val homeShortcuts by viewModel.shortcut.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    // App Drawer
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            AppDrawer(filteredApps, viewModel, { it ->
                searchQuery = it
            }) { Utils.launchApp(context, it.packageName) }

        }
    }

    // Home Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -20) { // swipe up
                        scope.launch { showSheet = true }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
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
        ViewPagerShortchuts(shorcuts, viewModel)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        getDialerAppPackageName(context)?.let {
            DockAppIcon(it, "Phone")
        }

        DockAppIcon("com.whatsapp", "Messages")

        getCameraAppPackageName(context)?.let {
            DockAppIcon(it, "Camera")
        }
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

    val iconBounds = remember { mutableStateOf(emptyMap<String, Rect>()) }

    LazyVerticalGrid(columns = GridCells.Fixed(4)) {
        items(gridItem.size, key = { it }) {
            val item = gridItem.get(it)
            when (item) {
                is LauncherItem.App -> DraggableAppIcon(
                    app = item,
                    viewModel,
                    iconBounds = iconBounds,
                    onDrop = { draggedApp, dropOffset ->
                        if (draggedApp is LauncherItem.App) {
                            val rect =
                                iconBounds.value[draggedApp.appInfo.packageName]
                                    ?: return@DraggableAppIcon
                            val dropX = dropOffset.x + rect.left
                            val dropY = dropOffset.y + rect.top
                            d("Drop", "Drop=($dropX,$dropY)")
                            iconBounds.value.forEach { (id, rect) ->
                                d("Drop", "Item=$id rect=$rect")
                            }
                            val target = iconBounds.value.entries.find { (id, rect) ->
                                d("Drop", "targetid=$id left=${rect.left} right=${rect.right}")
                                id != draggedApp.appInfo.packageName &&  // don’t match itself
                                        dropX in rect.left..rect.right &&
                                        dropY in rect.top..rect.bottom
                            }

                            val targetItem = gridItem.firstOrNull { it.id == target?.key }

                            if (targetItem != null) {
                                viewModel.handleDrop(draggedApp, targetItem)
                            }
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

fun getCameraAppPackageName(context: Context): String? {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val resolveInfo = context.packageManager.resolveActivity(
        cameraIntent,
        0
    )
    return resolveInfo?.activityInfo?.packageName
}

fun getDialerAppPackageName(context: Context): String? {
    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:123"))
    val resolveInfo = context.packageManager.resolveActivity(dialIntent, 0)
    return resolveInfo?.activityInfo?.packageName
}

