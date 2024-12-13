package com.capstone.fishguard.ui.komunitas

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.Lifecycle
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.capstone.fishguard.databinding.FragmentAddStoryBinding
import com.capstone.fishguard.reduceFileImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!

    private val addStoryViewModel: AddStoryViewModel by viewModels()

    private var currentPhotoPath: String? = null
    private var imageFile: File? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) openCamera()
        else showToast("Camera permission denied")
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) handleCapturedPhoto()
    }

    private val photoPicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> handleSelectedPhoto(uri) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        observeViewModel()
    }

    private fun setupButtons() {
        binding.cameraButton.setOnClickListener {
            if (hasCameraPermission()) openCamera()
            else requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.galleryButton.setOnClickListener { photoPicker.launch("image/*") }
        binding.buttonAdd.setOnClickListener { uploadStory() }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            setLoading(true)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                addStoryViewModel.uploadState.collect { state ->
                    setLoading(false)
                    handleUiState(state)
                }
            }
        }
    }

    private fun handleUiState(state: AddStoryUiState) {
        when (state) {
            is AddStoryUiState.Initial -> setLoading(false)
            is AddStoryUiState.Loading -> setLoading(true)
            is AddStoryUiState.Success -> {
                setLoading(false)
                showToast(state.message)
                findNavController().navigateUp()
            }
            is AddStoryUiState.Error -> {
                setLoading(false)
                showToast(state.message)
            }
        }
    }

    private fun uploadStory() {
        val description = binding.edAddDescription.text.toString().trim()

        when {
            description.isBlank() -> showToast("Deksripsi tidak boleh kosong")
            imageFile == null -> showToast("Silahkan pilih gambar")
            else -> {
                val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
                val imageRequestBody = imageFile!!.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile!!.name,
                    imageRequestBody
                )
                addStoryViewModel.uploadStory(descriptionRequestBody, imageMultipart)
            }
        }
    }

    private fun handleCapturedPhoto() {
        currentPhotoPath?.let { path ->
            imageFile = File(path).reduceFileImage()
            imageFile?.let { displayImage(it) }
        }
    }

    private fun handleSelectedPhoto(uri: Uri?) {
        uri?.let { selectedUri ->
            try {
                val inputStream = requireContext().contentResolver.openInputStream(selectedUri)
                val tempFile = File(
                    requireContext().cacheDir,
                    "selected_image_${System.currentTimeMillis()}.jpg"
                )
                tempFile.outputStream().use { fileOut -> inputStream?.copyTo(fileOut) }
                imageFile = tempFile.reduceFileImage()
                imageFile?.let { displayImage(it) }
            } catch (e: Exception) {
                showToast("Error selecting image: ${e.localizedMessage}")
            }
        }
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun openCamera() {
        val photoFile = createImageFile()
        currentPhotoPath = photoFile.absolutePath
        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        cameraLauncher.launch(photoUri)
    }

    private fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis().toString()
        val storageDir = requireContext().getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun displayImage(file: File) {
        Glide.with(requireContext())
            .load(file)
            .into(binding.previewImageView)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonAdd.isEnabled = !isLoading
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
