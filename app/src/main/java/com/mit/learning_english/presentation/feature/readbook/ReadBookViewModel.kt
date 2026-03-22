package com.mit.learning_english.presentation.feature.readbook

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.model.Chapter
import com.mit.learning_english.domain.usecase.GetChaptersByBookIdUseCase
import com.mit.learning_english.domain.usecase.GetPagesByChapterUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadBookViewModel @Inject constructor(
    private val getPagesByChapterUseCase: GetPagesByChapterUseCase,
    private val getChaptersByBookIdUseCase: GetChaptersByBookIdUseCase
) : BaseViewModel<ReadBookState, ReadBookEvent>(ReadBookState()) {
    fun loadInitialPages(bookId: Int, pageNumberLastRead: Int) {
        if (bookId != -1) {
            setState {
                copy(
                    pages = emptyMap(),
                    bookId = bookId,
                    pageNumberLastRead = pageNumberLastRead,
                    currentPageNumber = pageNumberLastRead,
                    isLoadingMore = false
                )
            }
        } else {
            setState { copy(errorMessage = "Lỗi không tìm thấy sách") }
            return
        }
        loadMorePages()
    }

    fun loadInitChapters(bookId: Int, pageNumberLastRead: Int) {
        viewModelScope.launch(exceptionHandler) {
            val result = getChaptersByBookIdUseCase(bookId)
            result.onSuccess { chapters ->
                setState { copy(chapters = chapters.sortedBy { it.number }) }
                updateActiveChapter(pageNumberLastRead)
            }
        }
    }

    private fun updateActiveChapter(pageNumber: Int) {
        val chapters = uiState.value.chapters
        if (chapters.isEmpty()) return
        var accumulatedPages = 0
        var foundChapter: com.mit.learning_english.domain.model.Chapter? = null

        for (chapter in chapters.sortedBy { it.number }) {
            accumulatedPages += chapter.totalPages
            if (pageNumber < accumulatedPages) {
                foundChapter = chapter
                break
            }
        }
        val activeChapter = foundChapter
        activeChapter?.let {
            setState { copy(activeChapterId = it.id) }
        }
    }

    fun loadMorePages() {
        val state = uiState.value
        if (state.isLoadingMore) return
        setState { copy(isLoadingMore = true) }
        val pagesNumbersNeedLoad = createPagesNumberNeedLoad()
        if (pagesNumbersNeedLoad.isNotEmpty()) {
            viewModelScope.launch(exceptionHandler) {
                val result = getPagesByChapterUseCase(state.bookId, pagesNumbersNeedLoad)
                result.onSuccess { data ->
                    setState { copy(isLoadingMore = false) }
                    val newPages = data.associateBy { it.number }
                    setState { copy(pages = (pages + newPages).toSortedMap()) }
                }
            }
        }
    }

    fun createPagesNumberNeedLoad(): List<Int> {
        val state = uiState.value
        val currentPageNumber = state.currentPageNumber
        val lastPage = state.lastPageNumberForBook
        val pages = state.pages
        val aroundSubPage: List<Int> = listOf(-3, -2, -1, 0, 1, 2, 3)
        return aroundSubPage.map { sub -> currentPageNumber + sub }.filter { pageNumber ->
            pageNumber >= 0 && pageNumber <= lastPage && !pages.containsKey(pageNumber)
        }
    }

    fun onPageChanged(position: Int) {
        val pageNumber = uiState.value.pages.values.toList().getOrNull(position)?.number ?: 0
        setState { copy(currentPageNumber = pageNumber) }
        updateActiveChapter(pageNumber)
        loadMorePages()
    }

    fun goToChapter(chapter: Chapter) {
        val state = uiState.value
        var firstNumberPagerChapter = 0
        
        for (it in state.chapters.sortedBy { it.number }) {
            if (chapter.id == it.id) {
                break
            }
            firstNumberPagerChapter += it.totalPages
        }
        
        setState { copy(currentPageNumber = firstNumberPagerChapter) }
        
        val pagesNumbersNeedLoad = createPagesNumberNeedLoad()
        if (pagesNumbersNeedLoad.isNotEmpty()) {
            setState { copy(isLoadingMore = true) }
            viewModelScope.launch(exceptionHandler) {
                val result = getPagesByChapterUseCase(state.bookId, pagesNumbersNeedLoad)
                result.onSuccess { data ->
                    setState { copy(isLoadingMore = false) }
                    val newPages = data.associateBy { it.number }
                    setState { copy(pages = (pages + newPages).toSortedMap()) }
                    
                    val updatedState = uiState.value
                    val position = updatedState.pages.values.toList().indexOfFirst { it.number == firstNumberPagerChapter }
                    if (position != -1) emitEvent(ReadBookEvent.GoToChapter(position))
                }
            }
        } else {
            val position = state.pages.values.toList().indexOfFirst { it.number == firstNumberPagerChapter }
            if (position != -1) emitEvent(ReadBookEvent.GoToChapter(position))
        }
    }


}

