package com.example.myapplication.ui.main


import android.app.Application
import android.content.Context
import android.content.pm.LauncherApps
import android.os.Process
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Utils.toEntity
import com.example.myapplication.data.local.entity.AppInfo
import com.example.myapplication.data.local.entity.LauncherItem
import com.example.myapplication.data.local.entity.LauncherItemEntity
import com.example.myapplication.data.repo.LauncherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    val repo: LauncherRepository, private val app: Application
) : ViewModel() {

    val shortcut: StateFlow<List<LauncherItemEntity>> =
        repo.getAllShortcutApps().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> =
        _installedApps.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadInstalledApps()
    }

    fun loadInstalledApps(context: Context): List<AppInfo> {
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

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {

            val launcherApps = app.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

            val apps = launcherApps.getActivityList(null, Process.myUserHandle()).map { info ->
                AppInfo(
                    label = info.label.toString(),
                    packageName = info.applicationInfo.packageName,
                    icon = info.getIcon(0),
                    user = info.user
                )
            }.sortedBy { it.label.lowercase() }

            _installedApps.value = apps
        }
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
        Log.d("Priya", "dragged $dragged  target $target")
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
                    insertItem(
                        LauncherItem.Folder(
                            name = "New Folder", apps = list
                        ).toEntity()
                    )
                }

            }
        }
    }
}
