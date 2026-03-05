package com.mit.learning_english.presentation.feature.main

import com.mit.learning_english.presentation.base.BaseUiState

data class MainState(
    override val isLoading: Boolean = false, override val errorMessage: String? = ""
) : BaseUiState<MainState> {
    override fun copyWith(
        isLoading: Boolean?, errorMessage: String?
    ): MainState {
        return copy(isLoading ?: this.isLoading, errorMessage ?: this.errorMessage)
    }
}