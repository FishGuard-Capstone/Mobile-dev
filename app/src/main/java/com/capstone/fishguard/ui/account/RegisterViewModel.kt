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

    // Sealed class untuk mengelola state registrasi
    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        data class Success(val response: RegisterResponse) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(email: String, password: String) {
        viewModelScope.launch {
            // Set state menjadi loading sebelum memulai registrasi
            _registerState.value = RegisterState.Loading

            try {
                // Gunakan Result dari repository
                val result = repository.register(email, password)

                result.onSuccess { response ->
                    // Registrasi berhasil
                    _registerState.value = RegisterState.Success(response)
                }.onFailure { exception ->
                    // Registrasi gagal
                    val errorMessage = when {
                        exception.message?.contains("Email sudah digunakan", true) == true ->
                            "Email sudah terdaftar. Gunakan email lain."
                        exception.message?.contains("Registrasi gagal", true) == true ->
                            "Registrasi gagal. Silakan coba lagi."
                        else ->
                            exception.message ?: "Registrasi gagal. Silakan coba lagi."
                    }
                    _registerState.value = RegisterState.Error(errorMessage)
                }
            } catch (e: Exception) {
                // Tangani kesalahan yang tidak terduga
                _registerState.value = RegisterState.Error(
                    e.localizedMessage ?: "Terjadi kesalahan. Silakan coba lagi."
                )
            }
        }
    }
}