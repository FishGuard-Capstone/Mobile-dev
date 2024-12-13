package com.capstone.fishguard.ui.komunitas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.fishguard.data.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {

    private val _uploadState = MutableStateFlow<AddStoryUiState>(AddStoryUiState.Initial)
    val uploadState: StateFlow<AddStoryUiState> = _uploadState.asStateFlow()

    fun uploadStory(description: RequestBody, imageFile: MultipartBody.Part) {
        viewModelScope.launch {
            _uploadState.value = AddStoryUiState.Loading
            try {
                val result = repository.uploadStory(description, imageFile)
                if (!result.error) {
                    _uploadState.value = AddStoryUiState.Success(result.message)
                } else {
                    _uploadState.value = AddStoryUiState.Error(result.message)
                }
            } catch (exception: Exception) {
                _uploadState.value = AddStoryUiState.Error(exception.localizedMessage ?: "An unknown error occurred")
            }
        }
    }
}

sealed class AddStoryUiState {
    data object Initial : AddStoryUiState()
    data object Loading : AddStoryUiState()
    data class Success(val message: String) : AddStoryUiState()
    data class Error(val message: String) : AddStoryUiState()
}