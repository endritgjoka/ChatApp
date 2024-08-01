package com.endritgjoka.chatapp.data.api

import com.endritgjoka.chatapp.data.model.User
import com.endritgjoka.chatapp.data.model.requests.LoginRequest
import com.endritgjoka.chatapp.data.model.requests.RegisterRequest
import com.endritgjoka.chatapp.data.model.responses.UserResponse
import com.endritgjoka.chatapp.data.utils.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatAPIService {
    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<ApiResponse<UserResponse>>

    @POST("login")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<ApiResponse<User>>
}