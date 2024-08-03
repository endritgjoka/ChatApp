package com.endritgjoka.chatapp.data.model.responses

import com.endritgjoka.chatapp.data.model.Conversation
import com.endritgjoka.chatapp.data.model.Message
import com.endritgjoka.chatapp.data.model.User
import com.google.gson.annotations.SerializedName

data class ConversationResponse(
    val conversation: Conversation ?= null,
    @SerializedName("last_message")
    val lastMessage: Message ?= null,
    val recipient:User
)