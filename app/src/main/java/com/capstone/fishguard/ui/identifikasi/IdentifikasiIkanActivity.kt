package com.capstone.fishguard.ui.identifikasi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.fishguard.databinding.ActivityIdentifikasiIkanBinding
import com.capstone.fishguard.helper.ImageClassifierHelper
import com.yalantis.ucrop.UCrop
import java.io.File

class IdentifikasiIkanActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener {
    private lateinit var viewBinding: ActivityIdentifikasiIkanBinding
    private var selectedImageUri: Uri? = null

    private val openGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val uniqueId = System.currentTimeMillis()
                val cropRequest = UCrop.of(it, Uri.fromFile(File(cacheDir, "cropped_$uniqueId.jpg")))
                cropRequest.start(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityIdentifikasiIkanBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewBinding.galleryButton.setOnClickListener {
            openImageGallery()
        }

        viewBinding.analyzeButton.setOnClickListener {
            if (selectedImageUri != null) {
                processImage()
            } else {
                displayMessage("Tidak Ada Gambar Terpilih")
            }
        }

        viewBinding.historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun openImageGallery() {
        openGallery.launch("image/*")
    }

    private fun displayImage() {
        selectedImageUri?.let {
            viewBinding.previewImageView.setImageURI(null)
            viewBinding.previewImageView.setImageURI(it)
        }
    }

    private fun processImage() {
        viewBinding.progressIndicator.visibility = View.VISIBLE
        selectedImageUri?.let {
            try {
                ImageClassifierHelper(
                    context = this,
                    classifierListener = this
                ).classifyStaticImage(it)
            } catch (e: Exception) {
                displayMessage("Error during classification: ${e.message}")
                viewBinding.progressIndicator.visibility = View.GONE
            }
        }
    }

    private fun navigateToResult(
        prediction: String,
        status: String,
        confidence: Int
    ) {
        selectedImageUri?.let {
            val resultIntent = Intent(this, ResultActivity::class.java).apply {
                putExtra("IMAGE_URI", it.toString())
                putExtra("PREDICTION", prediction)
                putExtra("STATUS", status)
                putExtra("CONFIDENCE_SCORE", confidence)
            }
            startActivity(resultIntent)
        }
    }

    override fun onError(error: String) {
        displayMessage(error)
        viewBinding.progressIndicator.visibility = View.GONE
    }

    override fun onResults(result: ImageClassifierHelper.ClassificationResult) {
        navigateToResult(result.label, result.status, result.confidence)
        viewBinding.progressIndicator.visibility = View.GONE
    }

    private fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null) {
            val croppedUri = UCrop.getOutput(data)
            if (croppedUri != null) {
                selectedImageUri = croppedUri
                displayImage()
            }
        }
    }
}