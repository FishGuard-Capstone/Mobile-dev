package com.capstone.fishguard.ui.identifikasi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.fishguard.databinding.ActivityIdentifikasiIkanBinding
import com.capstone.fishguard.helper.ImageClassifierHelper
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.IOException

class IdentifikasiIkanActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener {
    private lateinit var viewBinding: ActivityIdentifikasiIkanBinding
    private var selectedImageUri: Uri? = null
    private var photoFile: File? = null
    private var photoUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private val openGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val uniqueId = System.currentTimeMillis()
                try {
                    val cropRequest = UCrop.of(it, Uri.fromFile(File(cacheDir, "cropped_$uniqueId.jpg")))
                    cropRequest.start(this)
                } catch (e: Exception) {
                    Log.e("IdentifikasiIkanActivity", "Error in gallery image selection", e)
                    displayMessage("Gagal memilih gambar: ${e.localizedMessage}")
                }
            }
        }

    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                photoUri?.let { uri ->
                    val uniqueId = System.currentTimeMillis()
                    try {
                        val cropRequest = UCrop.of(uri, Uri.fromFile(File(cacheDir, "cropped_$uniqueId.jpg")))
                        cropRequest.start(this)
                    } catch (e: Exception) {
                        Log.e("IdentifikasiIkanActivity", "Error in camera image capture", e)
                        displayMessage("Gagal mengambil foto: ${e.localizedMessage}")
                    }
                }
            } else {
                displayMessage("Pengambilan foto dibatalkan")
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchCamera()
            } else {
                showPermissionRationaleDialog()
            }
        }

    private val settingsActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                launchCamera()
            } else {
                displayMessage("Izin kamera diperlukan untuk mengambil foto")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityIdentifikasiIkanBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Inisialisasi ImageClassifierHelper
        imageClassifierHelper = ImageClassifierHelper(this, this)

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewBinding.galleryButton.setOnClickListener {
            openImageGallery()
        }

        viewBinding.cameraButton.setOnClickListener {
            checkCameraPermission()
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

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> {
                showPermissionRationaleDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Izin Kamera Diperlukan")
            .setMessage("Aplikasi membutuhkan akses kamera untuk mengambil foto ikan. Apakah Anda ingin memberikan izin?")
            .setPositiveButton("Berikan Izin") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Buka Pengaturan") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                settingsActivityResultLauncher.launch(intent)
            }
            .setNeutralButton("Batal", null)
            .show()
    }

    private fun launchCamera() {
        try {
            photoFile = createImageFile()
            photoFile?.let { file ->
                photoUri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    file
                )
                photoUri?.let { uri ->
                    takePhoto.launch(uri)
                } ?: run {
                    displayMessage("Gagal membuat URI foto")
                }
            } ?: run {
                displayMessage("Gagal membuat file foto")
            }
        } catch (e: IOException) {
            Log.e("IdentifikasiIkanActivity", "Error creating image file", e)
            displayMessage("Gagal menyiapkan kamera: ${e.localizedMessage}")
        } catch (e: IllegalArgumentException) {
            Log.e("IdentifikasiIkanActivity", "Error with FileProvider", e)
            displayMessage("Kesalahan sistem saat mengakses kamera")
        }
    }

    private fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis().toString()
        val storageDir = getExternalFilesDir(null)
            ?: throw IOException("External storage is not available")
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
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
                imageClassifierHelper.classifyStaticImage(it)
            } catch (e: Exception) {
                Log.e("IdentifikasiIkanActivity", "Error during classification", e)
                displayMessage("Error during classification: ${e.localizedMessage}")
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

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
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