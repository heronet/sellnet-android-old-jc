package com.heronet.sellnetbeta.util

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Loading<T>(): Resource<T>(null, null)
    class Success<T>(data: T, message: String? = null): Resource<T>(data, message)
    class Error<T>(message: String, data: T?): Resource<T>(data, message)
}
