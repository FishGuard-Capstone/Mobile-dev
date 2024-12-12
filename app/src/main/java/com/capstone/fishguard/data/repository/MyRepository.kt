package com.capstone.fishguard.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.capstone.fishguard.data.remote.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MyRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    suspend fun register(email: String, password: String): Result<RegisterResponse> {
        val request = RegisterRequest(email, password)
        return try {
            val response = apiService.register(request)
            if (response.error) {
                // Mengembalikan error dengan pesan yang jelas
                Result.failure(Exception(response.message ?: "Registration failed"))
            } else {
                Result.success(response)
            }
        } catch (e: Exception) {
            // Menangani kesalahan jaringan atau kesalahan lainnya
            Log.e("RegisterError", "Registration failed: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(RegisterRequest(email, password))
            if (response.error) {
                // Mengembalikan error dengan pesan yang jelas
                Result.failure(Exception(response.message ?: "Login failed"))
            } else {
                // Simpan data pengguna jika login berhasil
                saveUserData(response.loginResult.token, response.loginResult.userId)
                Result.success(response)
            }
        } catch (e: Exception) {
            // Tangani kesalahan jaringan atau kesalahan lain yang mungkin terjadi
            Log.e("LoginError", "Login failed: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    // Save user data to DataStore
    private suspend fun saveUserData(token: String, userId: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
        }
    }

    // Get token from DataStore
    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences -> preferences[TOKEN_KEY] }
    }

    // Clear user data from DataStore
    suspend fun clearUserData() {
        dataStore.edit { it.clear() }
    }

    // Get all stories
    suspend fun getStories(): GetAllStoriesResponse {
        return apiService.getStories()
    }

    // Upload a story
    suspend fun uploadStory(
        description: RequestBody,
        imageFile: MultipartBody.Part
    ): AddStoryResponse {
        return apiService.uploadStory(description, imageFile)
    }
}
