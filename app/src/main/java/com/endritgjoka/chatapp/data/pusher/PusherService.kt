package com.endritgjoka.chatapp.data.pusher

import android.util.Log
import com.endritgjoka.chatapp.data.utils.AppPreferences
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpChannelAuthorizer

class PusherService() {
    var pusher:Pusher

    init{
        var options = PusherOptions()
        val headers: HashMap<String, String> = HashMap()

        options.setCluster("eu")
        options.setEncrypted(true)

        headers["Authorization"] = AppPreferences.authorization

        val BASE_URL = "http://192.168.100.160:8000/broadcasting/auth"

        val authorizer = HttpChannelAuthorizer(BASE_URL)
        authorizer.setHeaders(headers)
        options.channelAuthorizer = authorizer

        pusher = Pusher("2993cfc64ddd1e1e8924", options)
    }

    fun subscribeChannel(chanelName:String):PrivateChannel{
        return pusher.subscribePrivate(chanelName, object :PrivateChannelEventListener{
            override fun onEvent(event: PusherEvent?) {
                Log.i("MYTAG", event.toString())
            }

            override fun onSubscriptionSucceeded(channelName: String?) {
                Log.i("MYTAG", "Success: {$channelName}")
            }

            override fun onAuthenticationFailure(message: String?, e: java.lang.Exception?) {
                Log.i("MYTAG", "Error: "+message.toString())
            }

        })

    }

    fun unsubscribeChannel(chanelName:String){
         pusher.unsubscribe(chanelName)
    }




    fun connectPusher(){
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.i("MYTAG", "State changed from ${change.previousState} to ${change.currentState}")
            }

            override fun onError(
                message: String,
                code: String,
                e: Exception
            ) {
                Log.i("MYTAG", "There was a problem connecting! code ($code), message ($message), exception($e)")
            }
        }, ConnectionState.ALL)
    }

    fun disconnectPusher(){
        pusher.disconnect()
    }
}