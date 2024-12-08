package com.capstone.fishguard.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PredictionHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prediction: PredictionHistory)

    @Query("SELECT * FROM PredictionHistory ORDER BY id DESC")
    suspend fun getAll(): List<PredictionHistory>

    @Delete
    suspend fun delete(prediction: PredictionHistory)
}