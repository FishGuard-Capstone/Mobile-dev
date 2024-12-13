package com.capstone.fishguard.ui.komunitas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.fishguard.data.remote.Story
import com.capstone.fishguard.data.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListStoryViewModel @Inject constructor(
    private val storyRepository: MyRepository
) : ViewModel() {

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> = _errorState

    fun loadStories() {
        _loadingState.value = true
        viewModelScope.launch {
            try {
                val result = storyRepository.getStories()
                if (!result.error) {
                    _stories.value = result.listStory
                } else {
                    _errorState.value = result.message
                }
            } catch (exception: Exception) {
                _errorState.value = exception.message ?: "An unknown error occurred"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun clearErrorState() {
        _errorState.value = null
    }
}
