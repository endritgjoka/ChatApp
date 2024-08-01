package com.endritgjoka.chatapp.presentation.di

import com.endritgjoka.chatapp.data.repository.ChatRepositoryImpl
import com.endritgjoka.chatapp.data.repository.remoteDataSource.ChatRemoteDataSource
import com.endritgjoka.chatapp.domain.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun provideChatRepository(
       chatRemoteDataSource: ChatRemoteDataSource
    ): ChatRepository {
        return ChatRepositoryImpl(
            chatRemoteDataSource
        )
    }
}