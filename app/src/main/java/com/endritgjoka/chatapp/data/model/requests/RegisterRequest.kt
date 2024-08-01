package com.endritgjoka.chatapp.data.model.requests

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("full_name")
    val fullName:String,
    val email:String,
    val password:String,
    @SerializedName("password_confirmation")
    val passwordConfirmation:String
)