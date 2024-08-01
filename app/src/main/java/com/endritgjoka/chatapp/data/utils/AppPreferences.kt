package com.endritgjoka.chatapp.data.utils

import com.endritgjoka.chatapp.data.model.User
import com.endritgjoka.chatapp.presentation.ChatApp
import com.google.gson.Gson

object AppPreferences {
    private val preferences = ChatApp.getPreferences()

    var authorization: String
        get() = preferences.getString("authorization", "") ?: ""
        set(value) {
            preferences.edit().putString("authorization", "Bearer $value").apply()
            isLoggedIn = true;
        }

    var isLoggedIn: Boolean
        get() = preferences.getBoolean("isLoggedIn", false)
        set(value) = preferences.edit().putBoolean("isLoggedIn", value).apply()


    var activeUser: User?
        get() {
            val json = preferences.getString("activeUser", "") ?: ""
            return if (json.isNotEmpty()) {
                Gson().fromJson(json, User::class.java)
            } else {
                null
            }
        }
        set(user) {
            val json = Gson().toJson(user)
            preferences.edit().putString("activeUser", json).apply()
        }



    fun deleteUserData() {
        preferences.edit().remove("authorization").apply()
        preferences.edit().remove("activeUser").apply()
        preferences.edit().clear().apply()
        isLoggedIn = false;
    }

}
