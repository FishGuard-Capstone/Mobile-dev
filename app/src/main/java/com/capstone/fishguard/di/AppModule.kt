package com.capstone.fishguard.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.capstone.fishguard.BuildConfig
import com.capstone.fishguard.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(dataStore: DataStore<Preferences>): Interceptor {
        return Interceptor { chain ->
            val token = runBlocking {
                val tokenKey = stringPreferencesKey("auth_token")
                dataStore.data.map { preferences ->
                    preferences[tokenKey]
                }.first() ?: ""
            }
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token") // Format token
                .build()
            chain.proceed(newRequest)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                try {
                    val response = chain.proceed(chain.request())

                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string()
                        android.util.Log.e(
                            "NetworkError",
                            "Status Code: ${response.code}, Error Body: $errorBody"
                        )
                    }
                    response
                } catch (e: Exception) {
                    android.util.Log.e("NetworkError", "Network Error: ${e.message}")
                    throw e
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl("http://capstone-fish-guard.et.r.appspot.com/") // Sesuaikan dengan API
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }
}