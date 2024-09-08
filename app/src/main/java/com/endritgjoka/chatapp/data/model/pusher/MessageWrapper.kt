package com.endritgjoka.chatapp.data.model.pusher

import com.endritgjoka.chatapp.data.model.Message
import com.google.gson.annotations.SerializedName

data class MessageWrapper(
    var message: Message,
    @SerializedName("other_user_id")
    var otherUserId:Int,
    @SerializedName("other_user_name")
    var otherUserName:String,
    @SerializedName("conversation_type")
    var conversationType:String,
    @SerializedName("encryption_key")
    var encryptionKey:String,
    var unread_messages:Int,
)