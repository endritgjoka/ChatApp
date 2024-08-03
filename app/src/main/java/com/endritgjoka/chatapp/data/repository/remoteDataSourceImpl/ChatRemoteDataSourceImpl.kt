package com.endritgjoka.chatapp.data.repository.remoteDataSourceImpl

import com.endritgjoka.chatapp.data.api.ChatAPIService
import com.endritgjoka.chatapp.data.model.Message
import com.endritgjoka.chatapp.data.model.User
import com.endritgjoka.chatapp.data.model.requests.LoginRequest
import com.endritgjoka.chatapp.data.model.requests.MessageRequest
import com.endritgjoka.chatapp.data.model.requests.RegisterRequest
import com.endritgjoka.chatapp.data.model.responses.ConversationResponse
import com.endritgjoka.chatapp.data.model.responses.UserResponse
import com.endritgjoka.chatapp.data.repository.remoteDataSource.ChatRemoteDataSource
import com.endritgjoka.chatapp.data.utils.ApiResponse
import com.endritgjoka.chatapp.data.utils.AppPreferences
import retrofit2.Response

class ChatRemoteDataSourceImpl(private val chatAPIService: ChatAPIService): ChatRemoteDataSource {
    override suspend fun login(loginRequest: LoginRequest): Response<ApiResponse<UserResponse>> {
        return chatAPIService.login(loginRequest)
    }

    override suspend fun register(registerRequest: RegisterRequest): Response<ApiResponse<User>> {
        return chatAPIService.register(registerRequest)
    }

    override suspend fun searchUsers(query: String): Response<ApiResponse<ArrayList<ConversationResponse>>> {
        return chatAPIService.searchUsers(query, AppPreferences.authorization)
    }

    override suspend fun getConversations(): Response<ApiResponse<ArrayList<ConversationResponse>>> {
        return chatAPIService.getConversations(AppPreferences.authorization)
    }

    override suspend fun getConversationMessages(
        recipientId: Int
    ): Response<ApiResponse<ArrayList<Message>>> {
        return chatAPIService.getConversationMessages(recipientId, AppPreferences.authorization)
    }

    override suspend fun sendMessage(
        recipientId: Int,
        messageRequest: MessageRequest
    ): Response<ApiResponse<Message>> {
        return chatAPIService.sendMessage(recipientId, messageRequest, AppPreferences.authorization)
    }
}