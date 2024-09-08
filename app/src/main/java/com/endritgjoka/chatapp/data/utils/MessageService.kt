package com.endritgjoka.chatapp.data.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Throws(Exception::class)
fun decryptMessage(encryptedMessage: String?, encryptionKey: String?, iv: String?): String {
    if (!encryptedMessage.isNullOrEmpty() && !iv.isNullOrEmpty() && !encryptionKey.isNullOrEmpty()) {
        val ivBytes: ByteArray = Base64.decode(iv, Base64.DEFAULT)
        val encryptedBytes: ByteArray = Base64.decode(encryptedMessage, Base64.DEFAULT)
        val keyBytes: ByteArray = Base64.decode(encryptionKey, Base64.DEFAULT)

        if (keyBytes.size !in listOf(16, 24, 32)) {
            throw IllegalArgumentException("Invalid key length: ${keyBytes.size}")
        }

        val ivParameterSpec = IvParameterSpec(ivBytes)
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

        val original = cipher.doFinal(encryptedBytes)
        return String(original, Charsets.UTF_8)
    }
    return encryptedMessage.toString()
}