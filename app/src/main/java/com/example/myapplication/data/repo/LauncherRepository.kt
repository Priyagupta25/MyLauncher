package com.example.myapplication.data.repo

import com.example.myapplication.data.local.dao.LauncherItemDao
import com.example.myapplication.data.local.entity.LauncherItemEntity

import javax.inject.Inject


class LauncherRepository @Inject constructor(private val dao: LauncherItemDao) {
    fun getAllApps() = dao.getHomeItems()

     suspend fun deleteItem(item: LauncherItemEntity) {
         dao.deleteItem(item)
    }

    suspend fun insertItem(item: LauncherItemEntity) {
        dao.insertItem(item)
    }
    suspend fun updateFolder(item: LauncherItemEntity) {
        dao.updateFolder(item)
    }

}