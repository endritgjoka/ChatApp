package com.endritgjoka.chatapp.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endritgjoka.chatapp.data.model.requests.LoginRequest
import com.endritgjoka.chatapp.data.model.requests.RegisterRequest
import com.endritgjoka.chatapp.data.utils.ApiHandler
import com.endritgjoka.chatapp.data.utils.AppPreferences
import com.endritgjoka.chatapp.data.utils.Resource
import com.endritgjoka.chatapp.domain.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    val successResponse = MutableLiveData<String?>()
    val failedResponse = MutableLiveData<String?>()

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiHandler.safeApiCall { chatRepository.login(loginRequest) }

            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        AppPreferences.authorization = it.token
                        AppPreferences.activeUser = it.user
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        successResponse.postValue(response.message)
                    }
                }

                is Resource.Error -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        failedResponse.postValue(response.message)
                    }
                }

            }
        }
    }

     fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiHandler.safeApiCall { chatRepository.register(registerRequest) }

            when (response) {
                is Resource.Success -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        successResponse.postValue(response.message)
                    }
                }

                is Resource.Error -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        failedResponse.postValue(response.message)
                    }
                }

            }
        }
    }
}