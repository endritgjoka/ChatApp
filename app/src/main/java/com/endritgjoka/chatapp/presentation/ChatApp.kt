package com.endritgjoka.chatapp.presentation

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.pusher.client.channel.PrivateChannel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatApp : Application(){

    override fun onCreate() {
        super.onCreate()
        setApp(this)
    }
    companion object {
        lateinit var application: ChatApp
        var userChannel: PrivateChannel ?= null


        private fun setApp(app: ChatApp) {
            this.application = app
        }
        fun getPreferences(): SharedPreferences = application.getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
    }

}