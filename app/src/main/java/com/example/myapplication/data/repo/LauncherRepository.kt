package com.example.myapplication.data.repo

import com.example.myapplication.data.local.entity.LauncherItemEntity
import kotlinx.coroutines.flow.Flow

interface LauncherRepository {
    suspend fun insertItem(item: LauncherItemEntity)
    suspend fun deleteItem(item: LauncherItemEntity)
    suspend fun updateFolder(item: LauncherItemEntity)
    suspend fun updateFolderName(folderId: Int, newName: String)
    fun getAllShortcutApps(): Flow<List<LauncherItemEntity>>
}
