package com.example.myapplication.data.repo

import com.example.myapplication.data.local.dao.LauncherItemDao
import com.example.myapplication.data.local.entity.LauncherItemEntity

import javax.inject.Inject


class LauncherRepositoryImpl @Inject constructor(private val dao: LauncherItemDao) :
    LauncherRepository {
    override fun getAllShortcutApps() = dao.getHomeItems()

    override suspend fun deleteItem(item: LauncherItemEntity) {
        dao.deleteItem(item)
    }

    override suspend fun insertItem(item: LauncherItemEntity) {
        dao.insertItem(item)
    }

    override suspend fun updateFolder(item: LauncherItemEntity) {
        dao.updateFolder(item)
    }

    override suspend fun updateFolderName(folderId: Int, newName: String) {
        dao.updateFolderName(folderId, newName)
    }

}