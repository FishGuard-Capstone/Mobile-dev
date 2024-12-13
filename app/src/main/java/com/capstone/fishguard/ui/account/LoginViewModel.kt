package com.capstone.fishguard.ui.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.fishguard.data.remote.LoginResponse
import com.capstone.fishguard.data.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {

    // Sealed class untuk mengelola state login
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val response: LoginResponse) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private fun handleLoginError(exception: Throwable): String {
        Log.e("LoginError", "Login failed", exception)
        return when {
            exception is IOException -> "Koneksi jaringan bermasalah. Periksa internet Anda."
            exception is HttpException -> {
                when (exception.code()) {
                    404 -> "Pengguna tidak ditemukan"
                    401 -> "Email atau password salah"
                    500 -> "Masalah server. Silakan coba lagi nanti"
                    else -> "Terjadi kesalahan. Kode error: ${exception.code()}"
                }
            }
            exception.message?.contains("timeout", true) == true ->
                "Koneksi timeout. Periksa koneksi internet"
            exception.message?.contains("User tidak ditemukan", true) == true ->
                "Email atau password salah"
            exception.message?.contains("Unauthorized", true) == true ->
                "Email atau password salah"
            else -> exception.message ?: "Login gagal. Silakan coba lagi."
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Set state menjadi loading sebelum memulai login
            _loginState.value = LoginState.Loading

            try {
                // Gunakan Result dari repository
                val result = repository.login(email, password)

                result.onSuccess { response ->
                    // Login berhasil
                    _loginState.value = LoginState.Success(response)
                }.onFailure { exception ->
                    // Login gagal
                    val errorMessage = handleLoginError(exception)
                    _loginState.value = LoginState.Error(errorMessage)
                }
            } catch (e: Exception) {
                // Tangani kesalahan yang tidak terduga
                val errorMessage = handleLoginError(e)
                _loginState.value = LoginState.Error(errorMessage)
            }
        }
    }
}
