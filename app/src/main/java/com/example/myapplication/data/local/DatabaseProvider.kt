package com.example.myapplication.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var db: LauncherDatabase? = null
    fun getDatabase(context: Context): LauncherDatabase {
        return db ?: Room.databaseBuilder(
            context.applicationContext,
            LauncherDatabase::class.java,
            "launcher.db"
        ).build().also { db = it }
    }
}
