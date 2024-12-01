package com.capstone.fishguard.ui.newsapi

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.capstone.fishguard.ui.newsapi.NewsApiConfig.API_KEY
import com.capstone.fishguard.ui.newsapi.NewsApiClient.instance as apiClient

class NewsRepository {

    fun getNews(
        onSuccess: (List<NewsItem>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        apiClient.getNews("sea", "", "us", API_KEY)
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (response.isSuccessful) {
                        val articles = response.body()?.articles.orEmpty()
                        val processedNews = articles.mapNotNull { article ->
                            if (!article.title.isNullOrBlank()) {
                                NewsItem(
                                    title = article.title,
                                    urlToImage = article.urlToImage.orEmpty(),
                                    description = article.description.orEmpty()
                                )
                            } else null
                        }
                        onSuccess(processedNews)
                    } else {
                        onFailure("Failed to retrieve news: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, error: Throwable) {
                    onFailure("Network error: ${error.localizedMessage ?: "Unexpected error"}")
                }
            })
    }
}
