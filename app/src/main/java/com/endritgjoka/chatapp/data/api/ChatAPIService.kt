package com.endritgjoka.chatapp.data.api

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

interface ChatAPIService {
    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<ApiResponse<UserResponse>>

    @POST("register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<ApiResponse<User>>

    @POST("search/{query}")
    suspend fun searchUsers(
        @Path("query") query: String,
        @Header("Authorization") authorization:String
    ): Response<ApiResponse<ArrayList<ConversationResponse>>>

    @GET("conversations")
    suspend fun getConversations(
        @Header("Authorization") authorization:String
    ): Response<ApiResponse<ArrayList<ConversationResponse>>>

    @GET("messages/{recipient}")
    suspend fun getConversationMessages(
        @Path("recipient") recipientId: Int,
        @Header("Authorization") authorization:String
    ): Response<ApiResponse<ArrayList<Message>>>

    @POST("send-message/{recipient}")
    suspend fun sendMessage(
        @Path("recipient") recipientId: Int,
        @Body messageRequest: MessageRequest,
        @Header("Authorization") authorization:String
    ): Response<ApiResponse<Message>>
}