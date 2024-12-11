package com.capstone.fishguard.ui.identifikasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.fishguard.R
import com.capstone.fishguard.adapter.PredictionHistoryAdapter
import com.capstone.fishguard.database.PredictionHistoryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var database: PredictionHistoryDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        database = PredictionHistoryDatabase.getDatabase(this)
        loadPredictionHistory()
    }

    private fun loadPredictionHistory() {
        lifecycleScope.launch {
            val historyList = withContext(Dispatchers.IO) {
                database.predictionHistoryDao().getAll()
            }
            historyRecyclerView.adapter = PredictionHistoryAdapter(historyList)
        }
    }
}