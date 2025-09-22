package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.local.LauncherDatabase
import com.example.myapplication.data.local.dao.LauncherItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LauncherDatabase {
        return Room.databaseBuilder(
            context,
            LauncherDatabase::class.java,
            "launcher_db"
        ).fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    fun provideLauncherItemDao(db: LauncherDatabase): LauncherItemDao = db.launcherItemDao()


}
