package com.capstone.fishguard.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.capstone.fishguard.data.remote.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    // Menggunakan RegisterRequest yang berisi email dan password
    suspend fun register(email: String, password: String): RegisterResponse {
        // Membuat objek RegisterRequest untuk dikirimkan
        val request = RegisterRequest(email, password)
        return apiService.register(request) // Mengirim objek request
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val response = apiService.login(RegisterRequest(email, password)) // Menggunakan RegisterRequest
        if (!response.error) saveUserData(response.loginResult.token, response.loginResult.userId)
        return response
    }

    private suspend fun saveUserData(token: String, userId: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
        }
    }

    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences -> preferences[TOKEN_KEY] }
    }

    suspend fun clearUserData() {
        dataStore.edit { it.clear() }
    }

    suspend fun getStories(): GetAllStoriesResponse {
        return apiService.getStories()
    }

    suspend fun uploadStory(
        description: RequestBody,
        imageFile: MultipartBody.Part
    ): AddStoryResponse {
        return apiService.uploadStory(description, imageFile)
    }
}
