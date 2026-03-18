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
            if (body == null) return Result.Error("Empty response", response.code())

            // If the body is a BaseResponse wrapper, respect its statusCode/message
            return if (body is BaseResponse<*>) {
                val status = try { body.statusCode } catch (e: Exception) { response.code() }
                if (status in 200..299) {
                    // Success even if inner data is null (some endpoints return only message)
                    @Suppress("UNCHECKED_CAST")
                    Result.Success(body as T)
                } else {
                    Result.Error(body.message ?: "Server error", status)
                }
            } else {
                Result.Success(body)
            }
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

    fun <T> fromBaseResponse(response: Response<BaseResponse<T>>): Result<T> {
        if (response.isSuccessful) {
            val body = response.body()
            val data = body?.data
            return if (data != null) Result.Success(data)
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
}