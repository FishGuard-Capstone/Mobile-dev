package com.capstone.fishguard.ui.identifikasi

import android.net.Uri
import android.os.Bundle
import android.util.Log
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
    private lateinit var resultStatus: TextView
    private lateinit var resultDescription: TextView
    private lateinit var saveButton: MaterialButton

    private var isPredictionSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Initialize views
        resultImage = findViewById(R.id.result_image)
        resultName = findViewById(R.id.result_name)
        resultStatus = findViewById(R.id.result_status)
        resultDescription = findViewById(R.id.result_description)
        saveButton = findViewById(R.id.saveButton)

        // Get data from Intent with null checks
        val imageUri = intent.getStringExtra("IMAGE_URI")
        val prediction = intent.getStringExtra("PREDICTION") ?: "Tidak Dikenali"
        val status = intent.getStringExtra("STATUS") ?: "Tidak Tersedia"

        // Display data on layout
        imageUri?.let {
            try {
                resultImage.setImageURI(Uri.parse(it))
            } catch (e: Exception) {
                showToast("Gagal memuat gambar")
                Log.e("ResultActivity", "Error loading image", e)
            }
        }

        // Set text with additional description
        resultName.text = "Jenis Ikan: $prediction"
        resultStatus.text = "Status: $status"

        // Add description based on fish type
        val description = when (prediction) {
            "Ikan Balashark" -> getString(R.string.desc_balashark)
            "Ikan Belida" -> getString(R.string.desc_belida)
            "Ikan Lain" -> getString(R.string.desc_lain)
            "Ikan Pari Gergaji" -> getString(R.string.desc_pari_gergaji)
            "Ikan Pari Sungai" -> getString(R.string.desc_pari_sungai)
            "Ikan Raja Laut" -> getString(R.string.desc_raja_laut)
            else -> "Informasi tambahan tidak tersedia"
        }
        resultDescription.text = description

        // Configure save button
        setupSaveButton(imageUri, prediction, status)
    }

    private fun setupSaveButton(
        imageUri: String?,
        prediction: String,
        status: String
    ) {
        saveButton.setOnClickListener {
            if (!isPredictionSaved) {
                storePredictionInHistory(imageUri, prediction, status)
            } else {
                showToast("Prediksi sudah disimpan")
            }
        }
    }

    private fun storePredictionInHistory(
        imageUri: String?,
        prediction: String,
        status: String
    ) {
        // Validasi input
        if (imageUri.isNullOrEmpty()) {
            showToast("Tidak ada gambar untuk disimpan")
            return
        }

        val predictionHistory = PredictionHistory(
            imageUri = imageUri,
            prediction = prediction,
            status = status
        )

        val db = PredictionHistoryDatabase.getDatabase(this)
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Logging before insertion
                    Log.d("ResultActivity", "Attempting to insert: $predictionHistory")

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
                    Log.e("ResultActivity", "Error saving prediction", e)
                    showToast("Gagal menyimpan: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}