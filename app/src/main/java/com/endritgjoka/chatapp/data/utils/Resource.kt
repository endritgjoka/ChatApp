package com.endritgjoka.chatapp.data.utils

sealed class Resource<out T : Any> {
    class Success<T: Any>(val data: T?= null, val code: Int, val message: String) : Resource<T>()
    class Error<T: Any>(val code: Int, val message: String) : Resource<T>()
}