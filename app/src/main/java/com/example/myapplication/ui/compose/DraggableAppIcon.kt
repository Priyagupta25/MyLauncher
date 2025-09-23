package com.example.myapplication.ui.compose

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.Utils
import com.example.myapplication.Utils.toEntity
import com.example.myapplication.data.local.entity.LauncherItem
import com.example.myapplication.ui.main.LauncherViewModel
import kotlin.math.roundToInt


@Composable
fun DraggableAppIcon(
    app: LauncherItem.App,
    viewModel: LauncherViewModel,
    iconBounds: SnapshotStateMap<String, Rect>,
    onDrop: (LauncherItem, Offset) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .size(72.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .onGloballyPositioned { coords ->
                if (!isDragging) {
                    val rect = Rect(
                        coords.positionInRoot().x,
                        coords.positionInRoot().y,
                        coords.positionInRoot().x + coords.size.width,
                        coords.positionInRoot().y + coords.size.height
                    )
                    iconBounds[app.id] = rect
                }
            }
            .graphicsLayer(
                scaleX = if (isDragging) 1.2f else 1f,
                scaleY = if (isDragging) 1.2f else 1f,
                shadowElevation = if (isDragging) 8.dp.value else 0f
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { Utils.launchApp(context, app.appInfo.packageName) },
                    onLongPress = { expanded = true }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        onDrop(app, Offset(offsetX, offsetY))
                        offsetX = 0f
                        offsetY = 0f
                        isDragging = false
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragCancel = {
                        offsetX = 0f
                        offsetY = 0f
                        isDragging = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = app.appInfo.icon,
                contentDescription = app.appInfo.label,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.appInfo.label,
                fontSize = 12.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(48.dp)
            )
        }
    }

    if (expanded) {
        AppDropdownMenu(app = app, viewModel = viewModel) { expanded = false }
    }

}

@Composable
fun AppDropdownMenu(app: LauncherItem.App, viewModel: LauncherViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("App Info") },
            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
            onClick = {
                onDismiss()
                Utils.openAppInfo(context, app.appInfo.packageName)
            }
        )
        DropdownMenuItem(
            text = { Text("Delete") },
            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
            onClick = {
                onDismiss()
                viewModel.deleteItem(app.toEntity())
            }
        )
    }
}