package com.endritgjoka.chatapp.presentation.di

import com.endritgjoka.chatapp.data.api.ChatAPIService
import com.endritgjoka.chatapp.data.repository.remoteDataSource.ChatRemoteDataSource
import com.endritgjoka.chatapp.data.repository.remoteDataSourceImpl.ChatRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteDataModule {

    @Singleton
    @Provides
    fun provideChatRemoteDataSource(chatAPIService: ChatAPIService): ChatRemoteDataSource {
        return ChatRemoteDataSourceImpl(chatAPIService)
    }
}