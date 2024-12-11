package com.capstone.fishguard.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PredictionHistory::class], version = 6, exportSchema = false)
abstract class PredictionHistoryDatabase : RoomDatabase() {
    abstract fun predictionHistoryDao(): PredictionHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: PredictionHistoryDatabase? = null

        // Migrasi dari versi 5 ke 6
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Contoh migrasi: jika Anda ingin mengubah tipe data
                database.execSQL("ALTER TABLE PredictionHistory ADD COLUMN confidenceScore REAL DEFAULT 0.0")
            }
        }

        fun getDatabase(context: Context): PredictionHistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PredictionHistoryDatabase::class.java,
                    "database-history"
                )
                    .addMigrations(MIGRATION_5_6) // Gunakan migrasi manual
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}