package com.capstone.fishguard.ui.komunitas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.fishguard.data.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KomunitasViewModel @Inject constructor(
    private val userRepository: MyRepository
) : ViewModel() {

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState = _logoutState.asStateFlow()

    fun isUserLoggedIn() = userRepository.getToken().map { token -> !token.isNullOrBlank() }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading
            try {
                userRepository.clearUserData()
                val token = userRepository.getToken().first()
                _logoutState.value = if (token.isNullOrBlank()) LogoutState.Success else LogoutState.Error("Gagal menghapus sesi login")
            } catch (e: Exception) {
                _logoutState.value = LogoutState.Error(e.localizedMessage ?: "Logout gagal")
            }
        }
    }

    sealed class LogoutState {
        data object Idle : LogoutState()
        data object Loading : LogoutState()
        data object Success : LogoutState()
        data class Error(val errorMessage: String) : LogoutState()
    }
}