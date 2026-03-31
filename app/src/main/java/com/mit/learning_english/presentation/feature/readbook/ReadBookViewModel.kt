package com.mit.learning_english.presentation.feature.readbook

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.domain.model.Chapter
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.usecase.book.GetBookDetailByIdUseCase
import com.mit.learning_english.domain.usecase.page.GetPagesByBookUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReadBookViewModel @Inject constructor(
    private val getBookDetailByIdUseCase: GetBookDetailByIdUseCase,
    private val getPagesByBookUseCase: GetPagesByBookUseCase
) : BaseViewModel<ReadBookState, ReadBookEvent>(ReadBookState()) {

    private data class PagingParams(val bookId: Int, val totalPages: Int)

    private val pagingParams = MutableStateFlow<PagingParams?>(null)

    val pagesFlow: Flow<PagingData<Page>> = pagingParams
        .filterNotNull()
        .flatMapLatest { params ->
            getPagesByBookUseCase(params.bookId, params.totalPages)
        }
        .cachedIn(viewModelScope)

    fun loadInit(readBookArgs: ReadBookArgs) {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            val bookId = readBookArgs.bookId
            val chapterId = readBookArgs.chapterId
            val readModeValue = readBookArgs.readModeValue
            val result = getBookDetailByIdUseCase(bookId)
            result.onSuccess { bookDetail ->
                val totalPages = bookDetail.chapters.sumOf { it.totalPages }
                setState {
                    copy(
                        book = bookDetail,
                        chapters = bookDetail.chapters,
                        readMode = ReadMode.fromValue(readModeValue),
                        totalPages = totalPages
                    )
                }
                pagingParams.value = PagingParams(bookId, totalPages)

                if (chapterId != null) {
                    goToChapter(chapterId)
                }
            }.onError { e ->
                emitError(e.message ?: "Failed to load book detail")
            }
            setLoading(false)
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
        foundChapter?.let {
            setState { copy(activeChapterId = it.id) }
        }
    }

    fun onPageChanged(position: Int) {
        setState { copy(currentPageNumber = position) }
        updateActiveChapter(position)
    }

    fun goToChapter(chapterId: Int) {
        val chapters = uiState.value.chapters
        if (chapters.isEmpty()) return

        var firstPageOfChapter = 0
        for (chapter in chapters.sortedBy { it.number }) {
            if (chapterId == chapter.id) break
            firstPageOfChapter += chapter.totalPages
        }

        setState { copy(currentPageNumber = firstPageOfChapter, activeChapterId = chapterId) }
        emitEvent(ReadBookEvent.GoToChapter(firstPageOfChapter))
    }

    fun setReadMode(readMode: Int) {
        setState { copy(readMode = ReadMode.fromValue(readMode)) }
    }

    fun readModeClicked() {
        val currentReadValue = uiState.value.readMode.value
        setState { copy(readMode = ReadMode.fromValue(1 - currentReadValue)) }
    }
}
