package com.mit.learning_english.presentation.base

/**
 * Interface base cho UI State - chuẩn MVI.
 * Mọi UiState nên implement để có loading và error thống nhất.
 *
 * @param S Self type để copyWith trả về đúng kiểu concrete
 */
interface BaseUiState<out S : BaseUiState<S>> {
    val isLoading: Boolean
    val errorMessage: String?

    /**
     * Tạo bản copy với loading/error mới.
     * Data class: override fun copyWith(...) = copy(isLoading = loading ?: this.isLoading, errorMessage = error ?: this.errorMessage)
     */
    fun copyWith(isLoading: Boolean? = null, errorMessage: String? = null): S
}