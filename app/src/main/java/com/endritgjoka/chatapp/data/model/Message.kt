package com.endritgjoka.chatapp.data.model

import com.endritgjoka.chatapp.data.utils.decryptMessage
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class Message(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    val type:String,
    val iv: String,
    @SerializedName("encrypted_content")
    val encryptedContent:String,
    @SerializedName("encryption_key")
    val encryptionKey:String,
    @SerializedName("created_at")
    val createdAt:String,
    @SerializedName("updated_at")
    val updatedAt:String,
    val user: User,
){
    val formattedTime: String
        get() {
            return formatDate(createdAt)
        }

    var decryptedMessage:String = ""
        get() {
            return decryptMessage(encryptedContent, encryptionKey, iv)
        }

    fun formatDate(timestamp: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("HH:mma", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()
        val currentDate = Date()
        val parsedDate = inputFormat.parse(timestamp)
        val diffMillis = currentDate.time - parsedDate.time
        val oneDayMillis = 24 * 60 * 60 * 1000

        return when {
            diffMillis < oneDayMillis -> "Today " + outputFormat.format(parsedDate)
            diffMillis < 2 * oneDayMillis -> "Yesterday " + outputFormat.format(parsedDate)
            else -> "${diffMillis / oneDayMillis} days ago " + outputFormat.format(parsedDate)
        }
    }
}