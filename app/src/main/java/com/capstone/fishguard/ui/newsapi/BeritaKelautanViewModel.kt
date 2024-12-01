package com.capstone.fishguard.ui.newsapi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BeritaKelautanViewModel : ViewModel() {
    private val repository = NewsRepository()
    val news = MutableLiveData<List<NewsItem>>()
    private val errorMessage = MutableLiveData<String>()

    fun getNews() {
        viewModelScope.launch {
            repository.getNews(
                onSuccess = { fetchedNews ->
                    if (fetchedNews.isEmpty()) {
                        errorMessage.postValue("News data is empty.")
                    } else {
                        val filteredNews = fetchedNews.filter { it.title.isNotEmpty() }
                        news.postValue(filteredNews)
                    }
                },
                onFailure = { error ->
                    errorMessage.postValue("Error fetching news: $error")
                }
            )
        }
    }
}
