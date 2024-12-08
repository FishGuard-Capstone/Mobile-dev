package com.capstone.fishguard.ui.identifikasi

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.fishguard.R
import com.capstone.fishguard.database.PredictionHistory
import com.capstone.fishguard.database.PredictionHistoryDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {
    private lateinit var resultImage: ImageView
    private lateinit var resultText: TextView
    private lateinit var saveButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)



        // Inisialisasi view
        resultImage = findViewById(R.id.result_image)
        resultText = findViewById(R.id.result_text)
        saveButton = findViewById(R.id.saveButton)

        // Mendapatkan data dari Intent
        val imageUri = intent.getStringExtra("IMAGE_URI")
        val prediction = intent.getStringExtra("PREDICTION")
        val confidenceScore = intent.getIntExtra("CONFIDENCE_SCORE", 0)

        // Menampilkan data pada layout
        resultImage.setImageURI(Uri.parse(imageUri))
        resultText.text = "$prediction ($confidenceScore%)"

        // Tombol untuk menyimpan prediksi
        saveButton.setOnClickListener {
            storePredictionInHistory(imageUri, prediction, confidenceScore)
        }
    }

    private fun storePredictionInHistory(
        imageUri: String?,
        prediction: String?,
        confidenceScore: Int
    ) {
        val predictionHistory = PredictionHistory(
            imageUri = imageUri.orEmpty(),
            prediction = prediction.orEmpty(),
            confidenceScore = confidenceScore
        )

        val db = PredictionHistoryDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.predictionHistoryDao().insert(predictionHistory)

                // Ambil semua riwayat prediksi dan hapus yang lebih lama jika lebih dari 6
                val currentHistory = db.predictionHistoryDao().getAll()
                if (currentHistory.size > 6) {
                    val itemsToDelete = currentHistory.dropLast(6)
                    for (item in itemsToDelete) {
                        db.predictionHistoryDao().delete(item)
                    }
                }

                runOnUiThread { showToast("Prediksi berhasil disimpan") }
            } catch (e: Exception) {
                runOnUiThread { showToast("Gagal menyimpan: ${e.message}") }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
