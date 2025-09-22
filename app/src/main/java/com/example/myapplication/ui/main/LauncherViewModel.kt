package com.example.myapplication.ui.main


import android.content.Context
import android.content.pm.LauncherApps
import android.os.Process
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Utils.toEntity
import com.example.myapplication.data.local.entity.AppInfo
import com.example.myapplication.data.local.entity.LauncherItem
import com.example.myapplication.data.local.entity.LauncherItemEntity
import com.example.myapplication.data.repo.LauncherRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(val repo: LauncherRepository) : ViewModel() {

    val shortcut: StateFlow<List<LauncherItemEntity>> =
        repo.getAllShortcutApps()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getAllInstalledApps(context: Context):List<AppInfo> {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        return launcherApps.getActivityList(null, Process.myUserHandle()).map { info ->
            AppInfo(
                label = info.label.toString(),
                packageName = info.applicationInfo.packageName,
                icon = info.getIcon(0),
                user = info.user
            )
        }.sortedBy { it.label.lowercase() }

    }

    fun updateFolder(item: LauncherItemEntity) {
        GlobalScope.launch {
            repo.updateFolder(item)
        }
    }
    fun insertItem(item: LauncherItemEntity) {
        GlobalScope.launch {
            repo.insertItem(item)
        }
    }
    fun deleteItem(item: LauncherItemEntity) {
        GlobalScope.launch {
            repo.deleteItem(item)
        }
    }


    fun handleDrop(dragged: LauncherItem.App, target: LauncherItem) {
        when (target) {
            is LauncherItem.App -> {
                GlobalScope.launch {
                    deleteItem(dragged.toEntity())
                    deleteItem(target.toEntity())
                    insertItem(
                        LauncherItem.Folder(
                            name = "New Folder",
                            apps = mutableListOf(dragged.appInfo, target.appInfo)
                        ).toEntity()
                    )
                }
            }

            is LauncherItem.Folder -> {
                GlobalScope.launch {
                   deleteItem(dragged.toEntity())
                   deleteItem(target.toEntity())

                    val list = mutableListOf(dragged.appInfo)
                    list.addAll(target.apps)
                    updateFolder(
                        LauncherItem.Folder(
                            name = "New Folder",
                            apps = list
                        ).toEntity()
                    )
                }

            }
        }
    }
}
