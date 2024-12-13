package com.capstone.fishguard.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Response untuk menambahkan cerita
data class AddStoryResponse(
    val error: Boolean = false,
    val message: String = "",
    val storyId: String? = null
)

// Response untuk mendapatkan semua cerita
data class GetAllStoriesResponse(
    val error: Boolean = false,
    val message: String = "",
    val stories: List<Story> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val totalStories: Int = 0,
    val listStory: List<Story>
)

// Response untuk menyukai cerita
data class LikeResponse(
    val error: Boolean = false,
    val message: String = ""
)

// Response untuk menambahkan komentar
data class CommentResponse(
    val error: Boolean = false,
    val message: String = ""
)

// Model cerita individu
@Parcelize
data class Story(
    val id: String = "",
    val username: String = "",
    val caption: String = "",
    val imageUrl: String = "",
    val createdAt: String = "",
    val location: Location? = null,
    val likes: Int = 0,
    val description: String,
    val comments: List<Comment> = emptyList()
) : Parcelable

// Model lokasi (opsional)
@Parcelize
data class Location(
    val lat: Double? = null,
    val lon: Double? = null
) : Parcelable

// Model komentar individu
@Parcelize
data class Comment(
    val userId: String = "",
    val username: String = "",
    val text: String = "",
    val createdAt: String = ""
) : Parcelable
