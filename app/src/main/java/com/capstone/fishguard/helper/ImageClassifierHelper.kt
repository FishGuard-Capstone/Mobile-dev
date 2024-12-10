package com.capstone.fishguard.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException

class ImageClassifierHelper(
    private val context: Context,
    private val classifierListener: ClassifierListener?
) {
    private lateinit var interpreter: Interpreter
    private lateinit var imageProcessor: ImageProcessor
    private lateinit var inputImageBuffer: TensorImage
    private lateinit var outputProbabilityBuffer: TensorBuffer

    private val labels = listOf(
        "Ikan Balashark",
        "Ikan Raja Laut",
        "Ikan Belida",
        "Ikan Pari Sungai",
        "Ikan Pari Gergaji",
        "Ikan Lain"
    )

    // Threshold confidence untuk menentukan apakah gambar termasuk ikan dilindungi
    private val confidenceThreshold = 0.5f

    init {
        setupTFLiteInterpreter()
    }

    private fun setupTFLiteInterpreter() {
        try {
            val tfliteModel = FileUtil.loadMappedFile(context, "fishguardfinal.tflite")
            interpreter = Interpreter(tfliteModel)

            // Siapkan processor gambar untuk pra-pemrosesan
            imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(150, 150, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(NormalizeOp(0f, 255f))
                .build()

            // Inisialisasi buffer input dan output
            inputImageBuffer = TensorImage(DataType.FLOAT32)
            outputProbabilityBuffer = TensorBuffer.createFixedSize(intArrayOf(1, labels.size), DataType.FLOAT32)

        } catch (e: IOException) {
            classifierListener?.onError("Gagal memuat model: ${e.message}")
            Log.e(TAG, "Kesalahan pemuatan model", e)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun classifyStaticImage(imageUri: Uri) {
        try {
            // Dekode bitmap
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            }?.copy(Bitmap.Config.ARGB_8888, true)

            bitmap?.let { image ->
                // Pra-pemrosesan gambar
                inputImageBuffer.load(image)
                val processedImage = imageProcessor.process(inputImageBuffer)

                // Jalankan inferensi
                interpreter.run(processedImage.buffer, outputProbabilityBuffer.buffer)

                // Proses hasil
                val probabilities = outputProbabilityBuffer.floatArray
                val maxIndex = probabilities.indices.maxBy { probabilities[it] }
                val maxProbability = probabilities[maxIndex]
                val label = labels[maxIndex]

                // Daftar ikan yang dilindungi dengan indeks mereka
                val protectedFishIndices = listOf(0, 1, 2, 3, 4)

                val (finalLabel, finalStatus, confidence) = when {
                    // Jika probabilitas tertinggi rendah atau bukan ikan yang dikenali
                    maxProbability < confidenceThreshold || label == "Ikan Lain" -> {
                        Triple("Ikan Lain", "Tidak Dilindungi", 0)
                    }
                    // Jika ikan termasuk dalam daftar ikan dilindungi
                    maxIndex in protectedFishIndices -> {
                        Triple(label, "Dilindungi", (maxProbability * 100).toInt())
                    }
                    // Untuk kasus lainnya (seharusnya tidak terjadi)
                    else -> {
                        Triple("Ikan Lain", "Tidak Dilindungi", 0)
                    }
                }

                classifierListener?.onResults(
                    ClassificationResult(finalLabel, finalStatus, confidence)
                )
            } ?: classifierListener?.onError("Gagal memuat gambar")

        } catch (e: Exception) {
            classifierListener?.onError(e.message ?: "Kesalahan saat mengklasifikasi gambar")
        }
    }

    data class ClassificationResult(
        val label: String,
        val status: String,
        val confidence: Int
    )

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(result: ClassificationResult)
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}