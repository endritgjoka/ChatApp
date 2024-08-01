package com.endritgjoka.chatapp.data.utils

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    var message:String,
    @SerializedName("status_code")
    var statusCode:Int,
    var data: T? = null
)