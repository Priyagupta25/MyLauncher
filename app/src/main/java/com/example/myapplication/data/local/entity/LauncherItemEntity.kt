package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "launcher_items")
data class LauncherItemEntity(
    @PrimaryKey val id: Long,
    val type: LauncherItemType, // APP or FOLDER
    val label: String,
    val apps: List<AppInfo>
)



enum class LauncherItemType { APP, FOLDER }