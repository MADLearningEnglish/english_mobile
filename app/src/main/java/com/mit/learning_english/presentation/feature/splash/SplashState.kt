package com.mit.learning_english.presentation.feature.splash

import com.mit.learning_english.presentation.base.BaseUiState

data class SplashState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : BaseUiState<SplashState> {
    override fun copyWith(isLoading: Boolean?, errorMessage: String?): SplashState = copy(
        isLoading = isLoading ?: this.isLoading, errorMessage = errorMessage ?: this.errorMessage
    )
}
