package com.capstone.fishguard.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PredictionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val imageUri: String,
    val prediction: String,
    val confidenceScore: Int
)