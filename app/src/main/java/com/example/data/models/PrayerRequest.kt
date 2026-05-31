package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_requests")
data class PrayerRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userName: String,
    val userAvatar: String = "person", // Can be "person", "family_restroom", "favorite"
    val requestText: String,
    val category: String, // Salud, Gratitud, Trabajo, Paz, etc.
    val timestamp: Long = System.currentTimeMillis(),
    val timeAgo: String = "Hace un momento",
    val isUrgent: Boolean = false,
    val supportCount: Int = 0,
    val isPrayedByUser: Boolean = false,
    val isPublished: Boolean = true,
    val isArchived: Boolean = false
)
