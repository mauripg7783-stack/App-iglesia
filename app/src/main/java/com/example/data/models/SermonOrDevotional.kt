package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sermons_and_devotionals")
data class SermonOrDevotional(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "sermon", "devotional", "podcast"
    val title: String,
    val authorOrSubtitle: String,
    val passage: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val duration: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val currentTimeSeconds: Int = 0
)
