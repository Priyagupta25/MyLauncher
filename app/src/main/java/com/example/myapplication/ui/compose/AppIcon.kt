package com.example.myapplication.ui.compose

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.runtime.setValue
import com.example.myapplication.data.local.entity.AppInfo
import com.example.myapplication.Utils
import com.example.myapplication.Utils.getAppShortcuts
import com.example.myapplication.Utils.toEntity
import com.example.myapplication.data.local.entity.LauncherItem
import com.example.myapplication.ui.main.LauncherViewModel



@Composable
fun AppItem(
    app: AppInfo,
    viewModel: LauncherViewModel
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(72.dp)
            .combinedClickable(
                onClick = { Utils.launchApp(context, app.packageName) },
                onLongClick = { expanded = true }
            ),

        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = app.icon,
                contentDescription = app.label,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = app.label,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Add Shortcut") },
            leadingIcon = {
                Icon(Icons.Default.Edit, contentDescription = null)
            },
            onClick = {
                expanded = false
                viewModel.insertItem(LauncherItem.App(app).toEntity())
            }
        )
        DropdownMenuItem(
            text = { Text("App Info") },
            leadingIcon = {
                Icon(Icons.Default.Info, contentDescription = null)
            },
            onClick = {
                expanded = false
                Utils.openAppInfo(context, app.packageName)
            }
        )
        DropdownMenuItem(
            text = { Text("Edit") },
            leadingIcon = {
                Icon(Icons.Default.Edit, contentDescription = null)
            },
            onClick = {
                expanded = false
                // open edit UI (rename, shortcut, etc.)
            }
        )
        if (Utils.canUninstall(context, app.packageName)) {
            DropdownMenuItem(
                text = { Text("Uninstall") },
                leadingIcon = {
                    Icon(Icons.Default.Delete, contentDescription = null)
                },
                onClick = {
                    expanded = false
                    Utils.uninstallApp(context, app.packageName)
                    // open edit UI (rename, shortcut, etc.)
                }
            )
        }else{
            DropdownMenuItem(
                text = { Text("Pause app") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                onClick = {
                    expanded = false
                    Utils.disableApp(context, app.packageName)
                    // open edit UI (rename, shortcut, etc.)
                }
            )
        }
        if (Utils.hasAppWidgets(context, app.packageName)) {
            DropdownMenuItem(
                text = { Text("WIDGETS") },
                leadingIcon = {
                    Icon(Icons.Default.Menu, contentDescription = null)
                },
                onClick = {
                    expanded = false
                    Utils.openWidgetPicker(context, app.packageName)
                    // open edit UI (rename, shortcut, etc.)
                }
            )

        }
        if(Utils.hasShortcuts(context, app.packageName, app.user)){
            val shortcuts = remember { getAppShortcuts(context, app.packageName, app.user) }

            if (shortcuts.isNotEmpty()) {
                Text("Shortcuts", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(8.dp))

                shortcuts.forEach { shortcut ->
                    DropdownMenuItem(
                        text = { Text("WIDGETS") },
                        leadingIcon = {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        },
                        onClick = {
                            expanded = false
                            Utils.openWidgetPicker(context, app.packageName)
                            // open edit UI (rename, shortcut, etc.)
                        }
                    )
                }
            }
        }
    }

}