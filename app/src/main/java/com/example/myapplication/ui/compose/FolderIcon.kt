package com.example.myapplication.ui.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.Utils
import com.example.myapplication.data.local.entity.LauncherItem
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun FolderIcon(
    folder: LauncherItem.Folder,
    iconBounds: MutableState<Map<String, Rect>>
) {
    var showPopup by remember { mutableStateOf(false) }

    if (!showPopup) {

        Box(
            modifier = Modifier
                .size(72.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                    onTap = { showPopup = true},

                    )
                }
                .onGloballyPositioned { coords ->
                    val rect = Rect(
                        coords.positionInRoot().x,
                        coords.positionInRoot().y,
                        coords.positionInRoot().x + coords.size.width,
                        coords.positionInRoot().y + coords.size.height
                    )
                    iconBounds.value = iconBounds.value + (folder.name to rect)
                }
                .background(Color.LightGray, RoundedCornerShape(72.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    folder.apps.take(4).forEach { app ->
                        app.icon?.let {
                            DrawableIcon(it, Modifier.size(20.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = folder.name,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }else{
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = {showPopup =false},
            title = { Text(folder.name) },
            text = {
                LazyVerticalGrid(columns = GridCells.Fixed(4)) {
                    items(folder.apps.size) { i ->
                        val app = folder.apps.get(i)
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    Utils.launchApp(context, app.packageName)
                                    showPopup = false
                                           },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = rememberDrawablePainter(app.icon),
                                contentDescription = app.label,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(app.label, maxLines = 1)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

}


@Composable
fun DrawableIcon(drawable: Drawable, modifier: Modifier = Modifier) {
    Image(
        painter = rememberDrawablePainter(drawable = drawable),
        contentDescription = null,
        modifier = modifier
    )
}
