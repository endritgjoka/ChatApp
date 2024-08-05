package com.endritgjoka.chatapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endritgjoka.chatapp.data.model.Message
import com.endritgjoka.chatapp.data.model.requests.MessageRequest
import com.endritgjoka.chatapp.data.model.responses.ConversationResponse
import com.endritgjoka.chatapp.data.utils.ApiHandler
import com.endritgjoka.chatapp.data.utils.Resource
import com.endritgjoka.chatapp.domain.ChatRepository
import com.pusher.client.channel.PrivateChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
): ViewModel() {

    val allConversations = MutableLiveData<ArrayList<ConversationResponse>?>()
    val searchedConversations = MutableLiveData<ArrayList<ConversationResponse>?>()
    val failedFetchingConversations = MutableLiveData<String?>()

    val messages = MutableLiveData<ArrayList<Message>?>()
    val failedFetchingMessages = MutableLiveData<String?>()
    val clickedConversationRecipientId = MutableLiveData<Int?>()

    fun getConversationMessages(recipientId:Int){
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiHandler.safeApiCall { chatRepository.getConversationMessages(recipientId)}

            when (response) {
                is Resource.Success -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        messages.postValue(response.data)
                    }
                }

                is Resource.Error -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        failedFetchingMessages.postValue(response.message)
                    }
                }

            }
        }
    }


    fun searchUsers(query:String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiHandler.safeApiCall { chatRepository.searchUsers(query) }

            when (response) {
                is Resource.Success -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        response.data?.let {
                            searchedConversations.postValue(response.data)
                        }
                    }
                }

                is Resource.Error -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        failedFetchingConversations.postValue(response.message)
                    }
                }
            }
        }
    }

    fun getConversations(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiHandler.safeApiCall { chatRepository.getConversations() }

            when (response) {
                is Resource.Success -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        response.data?.let {
                            allConversations.postValue(response.data)
                        }
                    }
                }

                is Resource.Error -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        failedFetchingConversations.postValue(response.message)
                    }
                }
            }
        }
    }

    fun sendMessage(recipientId:Int,messageRequest: MessageRequest){
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiHandler.safeApiCall { chatRepository.sendMessage(recipientId,messageRequest) }

            when (response) {
                is Resource.Success -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        response.data?.let {

                        }
                    }
                }

                is Resource.Error -> {
                    CoroutineScope(Dispatchers.Main).launch {

                    }
                }
            }
        }
    }

    fun markConversationAsRead(recipientId:Int){
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiHandler.safeApiCall { chatRepository.markConversationAsRead(recipientId) }

            when (response) {
                is Resource.Success -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        response.data?.let {

                        }
                    }
                }

                is Resource.Error -> {
                    CoroutineScope(Dispatchers.Main).launch {

                    }
                }
            }
        }
    }

}