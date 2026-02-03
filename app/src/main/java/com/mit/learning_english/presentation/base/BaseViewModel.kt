package com.mit.learning_english.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    protected fun showLoading() {
        _isLoading.value = true
    }

    protected fun hideLoading() {
        _isLoading.value = false
    }

    protected open fun onError(throwable: Throwable) {
        hideLoading()
        _errorMessage.value = throwable.message ?: "Unknown Error"
        throwable.printStackTrace()
    }

    protected fun launchDataLoad(block: suspend () -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            showLoading()
            try {
                block()
            } finally {
                hideLoading()
            }
        }
    }
}
