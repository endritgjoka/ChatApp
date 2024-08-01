package com.endritgjoka.chatapp.data.repository.remoteDataSourceImpl

import com.endritgjoka.chatapp.data.api.ChatAPIService
import com.endritgjoka.chatapp.data.model.User
import com.endritgjoka.chatapp.data.model.requests.LoginRequest
import com.endritgjoka.chatapp.data.model.requests.RegisterRequest
import com.endritgjoka.chatapp.data.model.responses.UserResponse
import com.endritgjoka.chatapp.data.repository.remoteDataSource.ChatRemoteDataSource
import com.endritgjoka.chatapp.data.utils.ApiResponse
import com.endritgjoka.chatapp.domain.ChatRepository
import retrofit2.Response

class ChatRemoteDataSourceImpl(private val chatAPIService: ChatAPIService): ChatRemoteDataSource {
    override suspend fun login(loginRequest: LoginRequest): Response<ApiResponse<UserResponse>> {
        return chatAPIService.login(loginRequest)
    }

    override suspend fun register(registerRequest: RegisterRequest): Response<ApiResponse<User>> {
        return chatAPIService.register(registerRequest)
    }
}