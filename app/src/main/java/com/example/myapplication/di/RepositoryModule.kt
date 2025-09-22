package com.example.myapplication.di


import com.example.myapplication.data.local.dao.LauncherItemDao
import com.example.myapplication.data.repo.LauncherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideLauncherRepository(appDao: LauncherItemDao): LauncherRepository =
         LauncherRepository(appDao)
}
