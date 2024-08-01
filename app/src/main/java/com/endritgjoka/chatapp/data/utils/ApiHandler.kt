package com.endritgjoka.chatapp.data.utils

import android.util.Log
import com.endritgjoka.chatapp.R
import com.endritgjoka.chatapp.data.model.responses.ErrorResponse
import com.endritgjoka.chatapp.presentation.ChatApp
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ApiHandler {
    companion object {
        suspend fun <T : Any> safeApiCall(
            execute: suspend () -> Response<ApiResponse<T>>
        ): Resource<T> {

            return try {
                val response = execute()
                val body = response.body()
                Log.i("MYTAG", response.code().toString())
                Log.i("MYTAG", response.message().toString())

                if (response.isSuccessful && body != null) {
                    Resource.Success(
                        code = response.code(),
                        data = body.data,
                        message = body.message
                    )
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.errors
                    } catch (e: Exception) {
                        ChatApp.application.resources.getString(R.string.something_went_wrong)
                    }
                    Resource.Error(
                        code = response.code(),
                        message = errorMessage.let { it.toString() })
                }
            } catch (e: IOException) {
                Resource.Error(
                    code = 599, // commonly used status code for internet connection problem
                    message = ChatApp.application.resources.getString(R.string.no_internet_connection)
                )
            } catch (e: HttpException) {
                Resource.Error(code = e.code(), message = e.message())
            } catch (e: Exception) {
                Resource.Error(code = 500, message = e.message.toString())
            }
        }
    }
}