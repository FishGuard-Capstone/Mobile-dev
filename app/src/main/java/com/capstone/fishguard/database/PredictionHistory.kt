package com.capstone.fishguard.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PredictionHistory")
data class PredictionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUri: String,
    val prediction: String,
    val status: String,
    val confidenceScore: Int = 0, // Bisa tetap ada jika diperlukan, dengan default value
    val timestamp: Long = System.currentTimeMillis() // Tambahkan timestamp
)