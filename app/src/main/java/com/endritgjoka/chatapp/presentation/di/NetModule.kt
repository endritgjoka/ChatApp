package com.endritgjoka.chatapp.presentation.di

import com.endritgjoka.chatapp.data.api.ChatAPIService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetModule {
    private val BASE_URL = "http://192.168.0.111:8000/api/v1/"

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val gson = GsonBuilder().setLenient().create()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(60, TimeUnit.SECONDS) // Read timeout
            .writeTimeout(60, TimeUnit.SECONDS) // Write timeout
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .build()
    }


    @Singleton
    @Provides
    fun provideAuthApiService(retrofit: Retrofit): ChatAPIService {
        return retrofit.create(ChatAPIService::class.java)
    }
}