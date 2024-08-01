package com.endritgjoka.chatapp.domain

import com.endritgjoka.chatapp.data.model.User
import com.endritgjoka.chatapp.data.model.requests.LoginRequest
import com.endritgjoka.chatapp.data.model.requests.RegisterRequest
import com.endritgjoka.chatapp.data.model.responses.UserResponse
import com.endritgjoka.chatapp.data.utils.ApiResponse
import retrofit2.Response

interface ChatRepository {
    suspend fun login(loginRequest: LoginRequest): Response<ApiResponse<UserResponse>>
    suspend fun register(registerRequest: RegisterRequest): Response<ApiResponse<User>>
}
