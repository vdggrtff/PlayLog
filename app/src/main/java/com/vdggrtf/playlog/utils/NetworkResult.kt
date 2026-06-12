package com.vdggrtf.playlog.utils

import okio.IOException
import retrofit2.Response

sealed class NetworkResult<out  T> {

    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error<out T>(val message: String, val code: Int? = null) : NetworkResult<T>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T>{
    return try {
        val response = apiCall()
        if (response.isSuccessful && response.body() != null) {
            NetworkResult.Success(response.body()!!)
        } else {
            NetworkResult.Error("Server Error: ${response.message()} Code: ${response.code()}", code = response.code())
        }
    } catch (e: IOException){
        NetworkResult.Error("No internet Connection")
    } catch (e: Exception){
        NetworkResult.Error("Unknown exception ${e.localizedMessage}")

    }
}