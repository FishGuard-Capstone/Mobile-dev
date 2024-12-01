package com.capstone.fishguard.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    fun showComingSoonMessage() {
        _message.value = "Coming Soon!"
    }

    fun clearMessage() {
        _message.value = null
    }
}
