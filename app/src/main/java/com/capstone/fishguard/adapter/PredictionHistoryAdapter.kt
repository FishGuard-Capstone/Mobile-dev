package com.capstone.fishguard.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.fishguard.database.PredictionHistory
import com.capstone.fishguard.databinding.ItemPredictionHistoryBinding

class PredictionHistoryAdapter(private val historyItems: List<PredictionHistory>) :
    RecyclerView.Adapter<PredictionHistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPredictionHistoryBinding.inflate(inflater, parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bindData(historyItems[position])
    }

    override fun getItemCount(): Int {
        return historyItems.size
    }

    inner class HistoryViewHolder(private val binding: ItemPredictionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(history: PredictionHistory) {
            binding.apply {
                imageView.setImageURI(Uri.parse(history.imageUri))
                predictionTextView.text = history.prediction
                confidenceScoreTextView.text = "${history.confidenceScore}%"
            }
        }
    }
}
