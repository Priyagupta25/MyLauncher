package com.example.myapplication.data.local.entity

sealed class LauncherItem {
    abstract val id: String   // 👈 always available

        data class App(
            val appInfo: AppInfo
        ) : LauncherItem() {
            override val id: String = appInfo.packageName // 👈 stable
        }

        data class Folder(
            val name: String,
            val apps: MutableList<AppInfo>
        ) : LauncherItem() {
            override val id: String = "folder_${name}_${hashCode()}" // or UUID
        }
    }

