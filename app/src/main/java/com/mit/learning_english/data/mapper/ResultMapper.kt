package com.mit.learning_english.data.mapper

import com.google.gson.Gson
import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.domain.util.Result
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResultMapper @Inject constructor(private val gson: Gson) {
    fun <T> fromResponse(response: Response<T>): Result<T> {
        if (response.isSuccessful) {
            val body = response.body()
            return if (body != null) Result.Success(body)
            else Result.Error("Empty response", response.code())
        }
        val errorMessage = response.errorBody()?.string()?.let { json ->
            try {
                val errorResponse = gson.fromJson(json, BaseResponse::class.java)
                errorResponse.message
            } catch (e: Exception) {
                null
            }
        }
        return Result.Error(
            errorMessage ?: "Http error", response.code()
        )
    }

    fun <T> fromException(e: Throwable): Result<T> {
        return Result.Error(e.message ?: "Unknown error", exception = e)
    }
}