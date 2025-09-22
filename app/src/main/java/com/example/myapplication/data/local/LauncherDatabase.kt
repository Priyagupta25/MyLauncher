package com.example.myapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.data.local.util.Converters
import com.example.myapplication.data.local.dao.LauncherItemDao
import com.example.myapplication.data.local.entity.LauncherItemEntity

@Database(entities = [LauncherItemEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class LauncherDatabase : RoomDatabase() {
    abstract fun launcherItemDao(): LauncherItemDao
}