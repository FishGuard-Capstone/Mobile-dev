package com.capstone.fishguard.ui.identifikasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.capstone.fishguard.R
import com.capstone.fishguard.adapter.PredictionHistoryAdapter
import com.capstone.fishguard.database.PredictionHistoryDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val historyRecyclerView = findViewById<RecyclerView>(R.id.historyRecyclerView)
        val database = PredictionHistoryDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            val historyList = database.predictionHistoryDao().getAll()
            runOnUiThread {
                historyRecyclerView.adapter = PredictionHistoryAdapter(historyList)
            }
        }
    }
}
