package com.endritgjoka.chatapp.data.model

import com.google.gson.annotations.SerializedName

data class Conversation(
    val id: Int,
    @SerializedName("unread_messages")
    var unreadMessages:Int,
    val type:String,
    @SerializedName("last_message")
    var lastMessage: Message?,
    @SerializedName("sender_id")
    var senderId:Int,
    @SerializedName("recipient_id")
    val recipientId:Int,
    @SerializedName("profile_picture")
    val profilePicture:String?= null,
    @SerializedName("created_at")
    val createdAt:String,
    @SerializedName("updated_at")
    val updatedAt:String,
)