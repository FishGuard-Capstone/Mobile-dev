package com.capstone.fishguard.ui.identifikasi

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.fishguard.R
import com.capstone.fishguard.database.PredictionHistory
import com.capstone.fishguard.database.PredictionHistoryDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultActivity : AppCompatActivity() {
    private lateinit var resultImage: ImageView
    private lateinit var resultName: TextView
    private lateinit var resultConfidence: TextView
    private lateinit var resultStatus: TextView
    private lateinit var saveButton: MaterialButton

    private var isPredictionSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Initialize views
        resultImage = findViewById(R.id.result_image)
        resultName = findViewById(R.id.result_name)
        resultConfidence = findViewById(R.id.result_confidence)
        resultStatus = findViewById(R.id.result_status)
        saveButton = findViewById(R.id.saveButton)

        // Get data from Intent with null checks
        val imageUri = intent.getStringExtra("IMAGE_URI")
        val prediction = intent.getStringExtra("PREDICTION") ?: "Tidak Dikenali"
        val status = intent.getStringExtra("STATUS") ?: "Tidak Tersedia"
        val confidenceScore = intent.getIntExtra("CONFIDENCE_SCORE", 0)

        // Display data on layout
        imageUri?.let {
            try {
                resultImage.setImageURI(Uri.parse(it))
            } catch (e: Exception) {
                showToast("Gagal memuat gambar")
            }
        }

        // Set text with improved formatting
        resultName.text = "Jenis Ikan: $prediction"
        resultStatus.text = "Status: $status"
        resultConfidence.text = "Tingkat Kepercayaan: $confidenceScore%"

        // Configure save button
        setupSaveButton(imageUri, prediction, status, confidenceScore)
    }

    private fun setupSaveButton(
        imageUri: String?,
        prediction: String,
        status: String,
        confidenceScore: Int
    ) {
        saveButton.setOnClickListener {
            if (!isPredictionSaved) {
                storePredictionInHistory(imageUri, prediction, status, confidenceScore)
            } else {
                showToast("Prediksi sudah disimpan")
            }
        }
    }

    private fun storePredictionInHistory(
        imageUri: String?,
        prediction: String,
        status: String,
        confidenceScore: Int
    ) {
        // Validasi input
        if (imageUri.isNullOrEmpty()) {
            showToast("Tidak ada gambar untuk disimpan")
            return
        }

        val predictionHistory = PredictionHistory(
            imageUri = imageUri,
            prediction = prediction,
            status = status,
            confidenceScore = confidenceScore
        )

        val db = PredictionHistoryDatabase.getDatabase(this)
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Save new prediction
                    db.predictionHistoryDao().insert(predictionHistory)

                    // Manage history size
                    val currentHistory = db.predictionHistoryDao().getAll()
                    if (currentHistory.size > 6) {
                        val oldestItems = currentHistory.sortedBy { it.id }.take(currentHistory.size - 6)
                        db.predictionHistoryDao().deleteAll(oldestItems)
                    }
                }

                // Update UI on main thread
                withContext(Dispatchers.Main) {
                    showToast("Prediksi berhasil disimpan")
                    saveButton.isEnabled = false
                    saveButton.text = "Tersimpan"
                    isPredictionSaved = true
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Gagal menyimpan: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val IMAGE_URI_KEY = "IMAGE_URI"
        const val PREDICTION_KEY = "PREDICTION"
        const val STATUS_KEY = "STATUS"
        const val CONFIDENCE_SCORE_KEY = "CONFIDENCE_SCORE"
    }
}