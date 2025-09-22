package com.example.myapplication.ui.main


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Utils.toEntity
import com.example.myapplication.data.local.entity.LauncherItem
import com.example.myapplication.data.local.dao.LauncherItemDao
import com.example.myapplication.data.local.entity.LauncherItemEntity
import com.example.myapplication.data.repo.LauncherRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class LauncherViewModel( repo: LauncherRepository) : ViewModel() {

    val apps: StateFlow<List<LauncherItemEntity>> =
        repo.getAllApps()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun handleDrop(dragged: LauncherItem.App, target: LauncherItem, dao: LauncherItemDao) {
        when (target) {
            is LauncherItem.App -> {
                GlobalScope.launch {
                    dao.deleteItem(dragged.toEntity())
                    dao.deleteItem(target.toEntity())
                    dao.insertItem(
                        LauncherItem.Folder(
                            name = "New Folder",
                            apps = mutableListOf(dragged.appInfo, target.appInfo)
                        ).toEntity()
                    )
                }
            }

            is LauncherItem.Folder -> {
                GlobalScope.launch {
                    dao.deleteItem(dragged.toEntity())
                    dao.deleteItem(target.toEntity())

                   val list = mutableListOf(dragged.appInfo)
                    list.addAll(target.apps)
                    dao.updateFolder(
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

class AppViewModelFactory(
    private val repo: LauncherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LauncherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LauncherViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    }