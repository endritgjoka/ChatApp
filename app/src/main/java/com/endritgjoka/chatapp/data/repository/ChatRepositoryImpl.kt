package com.endritgjoka.chatapp.data.repository

import com.endritgjoka.chatapp.data.model.Conversation
import com.endritgjoka.chatapp.data.model.Message
import com.endritgjoka.chatapp.data.model.User
import com.endritgjoka.chatapp.data.model.requests.LoginRequest
import com.endritgjoka.chatapp.data.model.requests.MessageRequest
import com.endritgjoka.chatapp.data.model.requests.RegisterRequest
import com.endritgjoka.chatapp.data.model.responses.ConversationResponse
import com.endritgjoka.chatapp.data.model.responses.UserResponse
import com.endritgjoka.chatapp.data.repository.remoteDataSource.ChatRemoteDataSource
import com.endritgjoka.chatapp.data.utils.ApiResponse
import com.endritgjoka.chatapp.domain.ChatRepository
import retrofit2.Response

class ChatRepositoryImpl(private val chatRemoteDataSource: ChatRemoteDataSource): ChatRepository {
    override suspend fun login(loginRequest: LoginRequest): Response<ApiResponse<UserResponse>> {
        return chatRemoteDataSource.login(loginRequest)
    }

    override suspend fun register(registerRequest: RegisterRequest): Response<ApiResponse<User>> {
        return chatRemoteDataSource.register(registerRequest)
    }

    override suspend fun searchUsers(query: String): Response<ApiResponse<ArrayList<ConversationResponse>>> {
        return chatRemoteDataSource.searchUsers(query)
    }

    override suspend fun getConversations(): Response<ApiResponse<ArrayList<ConversationResponse>>> {
        return chatRemoteDataSource.getConversations()
    }

    override suspend fun getConversationMessages(
        recipientId: Int
    ): Response<ApiResponse<ArrayList<Message>>> {
        return chatRemoteDataSource.getConversationMessages(recipientId)
    }

    override suspend fun sendMessage(
        recipientId: Int,
        messageRequest: MessageRequest
    ): Response<ApiResponse<Message>> {
        return chatRemoteDataSource.sendMessage(recipientId, messageRequest)
    }

    override suspend fun markConversationAsRead(recipientId: Int): Response<ApiResponse<Conversation>> {
        return chatRemoteDataSource.markConversationAsRead(recipientId)
    }
}