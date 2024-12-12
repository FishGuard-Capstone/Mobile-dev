package com.capstone.fishguard.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.fishguard.data.remote.LoginResponse
import com.capstone.fishguard.data.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {

    private val _loginResult = MutableStateFlow<LoginResponse?>(null)
    val loginResult: StateFlow<LoginResponse?> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                _loginResult.value = response
            } catch (e: Exception) {
                _loginResult.value = LoginResponse(
                    error = true,
                    message = e.localizedMessage ?: "Login failed"
                )
            }
        }
    }
}