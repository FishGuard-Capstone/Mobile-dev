package com.capstone.fishguard.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Data class untuk request
data class RegisterRequest(
    val email: String,
    val password: String
)

interface ApiService {

    // Endpoint untuk registrasi
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest // Menggunakan request body untuk JSON
    ): RegisterResponse

    // Endpoint untuk login
    @POST("login")
    suspend fun login(
        @Body request: RegisterRequest // Menggunakan request body untuk JSON
    ): LoginResponse

    // Endpoint untuk mendapatkan semua cerita
    @GET("stories")
    suspend fun getStories(): GetAllStoriesResponse

    // Endpoint untuk mengupload cerita
    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): AddStoryResponse
}
