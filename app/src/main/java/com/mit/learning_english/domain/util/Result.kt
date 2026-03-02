package com.mit.learning_english.domain.util

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(
        val message: String,
        val code: Int? = null,
        val exception: Throwable? = null
    ) : Result<Nothing>()
    object Loading : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrDefault(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> data
        else -> defaultValue
    }

    suspend fun onSuccess(action: suspend (T) -> Unit): Result<T> {
        if (this is Success) {
            action(data)
        }
        return this
    }

    suspend fun onError(action: suspend (Error) -> Unit): Result<T> {
        if (this is Error) {
            action(this)
        }
        return this
    }

    suspend fun onLoading(action: suspend () -> Unit): Result<T> {
        if (this is Loading) {
            action()
        }
        return this
    }
}