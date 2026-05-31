package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.PrayerRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerRequestDao {
    @Query("SELECT * FROM prayer_requests WHERE isArchived = 0 AND isPublished = 1 ORDER BY isUrgent DESC, timestamp DESC")
    fun getPublishedRequests(): Flow<List<PrayerRequest>>

    @Query("SELECT * FROM prayer_requests WHERE isArchived = 0 AND isPublished = 0 ORDER BY timestamp DESC")
    fun getPendingRequests(): Flow<List<PrayerRequest>>

    @Query("SELECT * FROM prayer_requests WHERE userName = :userName ORDER BY timestamp DESC")
    fun getRequestsByUserName(userName: String): Flow<List<PrayerRequest>>

    @Query("SELECT COUNT(*) FROM prayer_requests WHERE isArchived = 0 AND isPublished = 0")
    fun getPendingRequestsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: PrayerRequest): Long

    @Update
    suspend fun updateRequest(request: PrayerRequest)

    @Query("DELETE FROM prayer_requests WHERE id = :id")
    suspend fun deleteRequestById(id: Int)
}
