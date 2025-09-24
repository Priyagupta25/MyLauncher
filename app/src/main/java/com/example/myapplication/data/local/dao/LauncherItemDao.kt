package com.example.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.local.entity.LauncherItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: LauncherItemEntity): Long

    @Query("UPDATE launcher_items SET label = :newName WHERE id = :folderId")
    suspend fun updateFolderName(folderId: Int, newName: String)
    @Update
    suspend fun updateFolder(folder: LauncherItemEntity)

    @Delete
    suspend fun deleteItem(item: LauncherItemEntity)

    @Query("SELECT * FROM launcher_items ")
     fun getHomeItems(): Flow<List<LauncherItemEntity>>


}