package com.capstone.fishguard.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PredictionHistory::class], version = 5, exportSchema = false)
abstract class PredictionHistoryDatabase : RoomDatabase() {
    abstract fun predictionHistoryDao(): PredictionHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: PredictionHistoryDatabase? = null

        fun getDatabase(context: Context): PredictionHistoryDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PredictionHistoryDatabase::class.java,
                    "database-history"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}