package com.example.network.helpers

import com.example.network.model.responses.ApiErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class NetworkHelper @Inject constructor() {

    suspend fun <T> safeApiCall(apiCall: suspend () -> T): ResultWrapper<T>  =
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorResponse(throwable)
                        ResultWrapper.GenericError(code, errorResponse)
                    }
                    else -> {
                        ResultWrapper.GenericError(null, null)
                    }
                }
            }

    private fun convertErrorResponse(throwable: HttpException): ApiErrorResponse? {
        return try {
            throwable.response()?.errorBody()?.charStream().let {
                return Gson().fromJson(it, ApiErrorResponse::class.java)
            }
        } catch (ex: Exception) {
            null
        }
    }
}