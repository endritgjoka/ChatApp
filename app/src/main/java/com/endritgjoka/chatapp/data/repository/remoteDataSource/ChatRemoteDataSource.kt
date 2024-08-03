package com.endritgjoka.chatapp.data.repository.remoteDataSource

import com.endritgjoka.chatapp.data.model.Message
import com.endritgjoka.chatapp.data.model.User
import com.endritgjoka.chatapp.data.model.requests.LoginRequest
import com.endritgjoka.chatapp.data.model.requests.MessageRequest
import com.endritgjoka.chatapp.data.model.requests.RegisterRequest
import com.endritgjoka.chatapp.data.model.responses.ConversationResponse
import com.endritgjoka.chatapp.data.model.responses.UserResponse
import com.endritgjoka.chatapp.data.utils.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatRemoteDataSource {
    suspend fun login(loginRequest: LoginRequest): Response<ApiResponse<UserResponse>>
    suspend fun register(registerRequest: RegisterRequest): Response<ApiResponse<User>>
    suspend fun searchUsers(query: String, ): Response<ApiResponse<ArrayList<ConversationResponse>>>
    suspend fun getConversations(): Response<ApiResponse<ArrayList<ConversationResponse>>>
    suspend fun getConversationMessages(recipientId: Int): Response<ApiResponse<ArrayList<Message>>>
    suspend fun sendMessage(recipientId: Int, messageRequest: MessageRequest): Response<ApiResponse<Message>>
}