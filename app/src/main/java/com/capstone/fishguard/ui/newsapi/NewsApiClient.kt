package com.capstone.fishguard.ui.newsapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NewsApiClient {

    val instance: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NewsApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }
}
