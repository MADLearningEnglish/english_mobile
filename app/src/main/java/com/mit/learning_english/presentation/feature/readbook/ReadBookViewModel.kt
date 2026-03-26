package com.mit.learning_english.presentation.feature.readbook

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.model.Chapter
import com.mit.learning_english.domain.usecase.GetBookDetailByIdUseCase
import com.mit.learning_english.domain.usecase.GetPagesByChapterUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadBookViewModel @Inject constructor(
    private val getPagesByChapterUseCase: GetPagesByChapterUseCase,
    private val getBookDetailByIdUseCase: GetBookDetailByIdUseCase
) : BaseViewModel<ReadBookState, ReadBookEvent>(ReadBookState()) {
    fun loadInit(readBookArgs: ReadBookArgs) {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            val bookId = readBookArgs.bookId
            val chapterId = readBookArgs.chapterId
            val readModeValue = readBookArgs.readModeValue
            val result = getBookDetailByIdUseCase(bookId)

            result.onSuccess { bookDetail ->
                setState {
                    copy(
                        book = bookDetail,
                        chapters = bookDetail.chapters,
                        readMode = ReadMode.fromValue(readModeValue)
                    )
                }
                if (chapterId == null) {
                    loadMorePages()
                } else {
                    goToChapter(chapterId)
                }
            }.onError { e ->
                emitError(e.message ?: "Failed to load book detail")
            }
            setLoading(false)
            setState { copy(readMode = ReadMode.fromValue(readModeValue)) }
        }
    }

    private fun updateActiveChapter(pageNumber: Int) {
        val chapters = uiState.value.chapters
        if (chapters.isEmpty()) return
        var accumulatedPages = 0
        var foundChapter: Chapter? = null

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
        val book = state.book ?: return

        setState { copy(isLoadingMore = true) }
        val pagesNumbersNeedLoad = createPagesNumberNeedLoad()
        if (pagesNumbersNeedLoad.isNotEmpty()) {
            viewModelScope.launch(exceptionHandler) {
                val result = getPagesByChapterUseCase(book.id, pagesNumbersNeedLoad)
                result.onSuccess { data ->
                    setState { copy(isLoadingMore = false) }
                    val newPages = data.associateBy { it.number }
                    setState { copy(pages = (pages + newPages).toSortedMap()) }
                }.onError {
                    setState { copy(isLoadingMore = false) }
                }
            }
        } else {
            setState { copy(isLoadingMore = false) }
        }
    }

    fun createPagesNumberNeedLoad(pageNumber: Int? = null): List<Int> {
        val state = uiState.value
        val currentPageNumber = pageNumber ?: state.currentPageNumber
        val lastPageNumber: Int = state.book?.chapters?.sumOf { it.totalPages } ?: 0
        val pages = state.pages
        val aroundSubPage: List<Int> = listOf(-3, -2, -1, 0, 1, 2, 3)
        return aroundSubPage.map { sub -> currentPageNumber + sub }.filter { pageNum ->
            pageNum >= 0 && pageNum < lastPageNumber && !pages.containsKey(pageNum)
        }
    }

    fun onPageChanged(position: Int) {
        val pageList = uiState.value.pages.values.toList()
        pageList.getOrNull(position)?.let { page ->
            setState { copy(currentPageNumber = page.number) }
            updateActiveChapter(page.number)
            loadMorePages()
        }
    }

    fun goToChapter(chapterId: Int) {
        val state = uiState.value
        val chapters = state.chapters
        if (chapters.isEmpty()) return
        var firstNumberPagerChapter = 0
        for (it in chapters.sortedBy { it.number }) {
            if (chapterId == it.id) break
            firstNumberPagerChapter += it.totalPages
        }

        setState { copy(currentPageNumber = firstNumberPagerChapter) }
        val book = state.book ?: return
        val pagesNumbersNeedLoad = createPagesNumberNeedLoad(firstNumberPagerChapter)

        if (pagesNumbersNeedLoad.isNotEmpty()) {
            setState { copy(isLoadingMore = true) }
            viewModelScope.launch(exceptionHandler) {
                val result = getPagesByChapterUseCase(book.id, pagesNumbersNeedLoad)
                result.onSuccess { data ->
                    setState { copy(isLoadingMore = false) }
                    val newPages = data.associateBy { it.number }
                    setState { copy(pages = (pages + newPages).toSortedMap()) }
                    val updatedState = uiState.value
                    val position = updatedState.pages.values.toList()
                        .indexOfFirst { it.number == firstNumberPagerChapter }
                    if (position != -1) emitEvent(ReadBookEvent.GoToChapter(position))
                }.onError {
                    setState { copy(isLoadingMore = false) }
                }
            }
        } else {
            val position =
                state.pages.values.toList().indexOfFirst { it.number == firstNumberPagerChapter }
            if (position != -1) emitEvent(ReadBookEvent.GoToChapter(position))
        }
    }

    fun setReadMode(readMode: Int) {
        setState { copy(readMode = ReadMode.fromValue(readMode)) }
    }
}
