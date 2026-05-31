package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.models.PrayerRequest
import com.example.data.models.SermonOrDevotional

@Database(entities = [PrayerRequest::class, SermonOrDevotional::class], version = 1, exportSchema = false)
abstract class SacredGroveDatabase : RoomDatabase() {
    abstract fun prayerRequestDao(): PrayerRequestDao
    abstract fun sermonOrDevotionalDao(): SermonOrDevotionalDao

    companion object {
        @Volatile
        private var INSTANCE: SacredGroveDatabase? = null

        fun getDatabase(context: Context): SacredGroveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SacredGroveDatabase::class.java,
                    "sacred_grove_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
