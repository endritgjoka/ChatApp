package com.endritgjoka.chatapp.data.model.responses

import com.endritgjoka.chatapp.data.model.User

data class UserResponse(
    val token: String,
    val user: User
)