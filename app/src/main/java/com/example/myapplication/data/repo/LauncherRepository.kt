package com.example.myapplication.data.repo

import com.example.myapplication.data.local.dao.LauncherItemDao


class LauncherRepository(private val dao: LauncherItemDao) {
    fun getAllApps() = dao.getHomeItems()

}