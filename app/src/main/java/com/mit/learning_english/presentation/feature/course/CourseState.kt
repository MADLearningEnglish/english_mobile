package com.mit.learning_english.presentation.feature.course

import com.mit.learning_english.presentation.base.BaseUiState

data class CourseState(
    override val isLoading: Boolean = false, override val errorMessage: String? = null
) : BaseUiState<CourseState> {
    override fun copyWith(
        isLoading: Boolean?, errorMessage: String?
    ): CourseState {
        return copy(isLoading = isLoading ?: this.isLoading, errorMessage ?: this.errorMessage)
    }
}