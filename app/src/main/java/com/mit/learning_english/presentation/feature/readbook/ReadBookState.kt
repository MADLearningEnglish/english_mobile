package com.mit.learning_english.presentation.feature.readbook

import com.mit.learning_english.domain.model.Chapter
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.presentation.base.BaseUiState

data class ReadBookState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val isPlaying: Boolean = false,
    val currentTime: Int = 0,
    val currentAudio: String? = null,
    val readMode: ReadMode = ReadMode.ReadMode,
    val currentPageNumber: Int = 0,
    val bookId: Int = -1,
    val pageNumberLastRead: Int = -1,
    val pages: Map<Int, Page> = sortedMapOf(),
    val isLoadingMore: Boolean = false,
    val hasMorePage: Boolean = true,
    val chapters: List<Chapter> = emptyList(),
    val lastPageNumberForBook: Int = 1,
    val activeChapterId: Int? = null,
) : BaseUiState<ReadBookState> {
    override fun copyWith(
        isLoading: Boolean?, errorMessage: String?
    ): ReadBookState {
        return this.copy(isLoading = isLoading ?: this.isLoading, errorMessage ?: this.errorMessage)
    }
}