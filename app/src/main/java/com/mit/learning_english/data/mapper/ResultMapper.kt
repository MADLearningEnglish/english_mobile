package com.mit.learning_english.data.mapper

import retrofit2.Response
import com.mit.learning_english.domain.util.Result

object ResultMapper {
    fun <T> fromResponse(response: Response<T>): Result<T> {
        if (response.isSuccessful) {
            val body = response.body()
            return if (body != null) Result.Success(body)
            else Result.Error("Empty response", response.code())
        }
        return Result.Error("Http error", response.code())
    }

    fun <T> fromException(e: Throwable): Result<T> {
        return Result.Error(e.message ?: "Unknown error", exception = e)
    }
}