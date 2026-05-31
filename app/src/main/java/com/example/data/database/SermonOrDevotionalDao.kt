package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.SermonOrDevotional
import kotlinx.coroutines.flow.Flow

@Dao
interface SermonOrDevotionalDao {
    @Query("SELECT * FROM sermons_and_devotionals ORDER BY id ASC")
    fun getAll(): Flow<List<SermonOrDevotional>>

    @Query("SELECT * FROM sermons_and_devotionals WHERE type = :type ORDER BY id ASC")
    fun getByType(type: String): Flow<List<SermonOrDevotional>>

    @Query("SELECT * FROM sermons_and_devotionals WHERE isSaved = 1")
    fun getFavorites(): Flow<List<SermonOrDevotional>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SermonOrDevotional>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SermonOrDevotional)

    @Update
    suspend fun update(item: SermonOrDevotional)
}
