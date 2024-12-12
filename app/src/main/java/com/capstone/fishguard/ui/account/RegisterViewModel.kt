package com.capstone.fishguard.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.fishguard.data.remote.RegisterResponse
import com.capstone.fishguard.data.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {

    private val _registerResult = MutableStateFlow<RegisterResponse?>(null)
    val registerResult: StateFlow<RegisterResponse?> = _registerResult

    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.register(email, password)
                _registerResult.value = response
            } catch (e: Exception) {
                // Mengembalikan RegisterResponse dengan error yang sesuai
                _registerResult.value = RegisterResponse(
                    message = e.localizedMessage ?: "Registration failed",
                    token = "",
                    error = true
                )
            }
        }
    }
}