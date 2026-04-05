package com.mit.learning_english.presentation.feature.readbook

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.domain.model.Audio
import com.mit.learning_english.domain.model.Chapter
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.usecase.book.GetBookDetailByIdUseCase
import android.os.SystemClock
import com.mit.learning_english.domain.usecase.book.UpdateBookReadingProgressUseCase
import com.mit.learning_english.domain.usecase.page.GetPagesByBookUseCase
import com.mit.learning_english.domain.usecase.page.LookupTextUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.shared.Constant
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
    private val getPagesByBookUseCase: GetPagesByBookUseCase,
    private val lookupTextUseCase: LookupTextUseCase,
    private val updateBookReadingProgressUseCase: UpdateBookReadingProgressUseCase
) : BaseViewModel<ReadBookState, ReadBookEvent>(ReadBookState()) {

    private data class PagingParams(val bookId: Int, val totalPages: Int, val initialKey: Int)

    private val pagingParams = MutableStateFlow<PagingParams?>(null)

    /** Thời điểm bắt đầu phiên đọc (foreground), để gửi durationSeconds lên server. */
    private var readingSessionStartElapsed: Long? = null

    val pagesFlow: Flow<PagingData<Page>> = pagingParams
        .filterNotNull()
        .flatMapLatest { params ->
            getPagesByBookUseCase(params.bookId, params.totalPages, params.initialKey)
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
                val sortedChapters = bookDetail.chapters.sortedBy { it.number }
                val resolvedChapterId = chapterId ?: sortedChapters.firstOrNull()?.id
                setState {
                    copy(
                        book = bookDetail,
                        chapters = sortedChapters,
                        readMode = ReadMode.fromValue(readModeValue),
                        totalPages = totalPages
                    )
                }

                val firstPage = if (chapterId != null) {
                    calculateFirstPage(chapterId, sortedChapters)
                } else {
                    bookDetail.lastReadNumberPage
                }
                val initialKey = (firstPage / Constant.PAGE_SIZE_PAGE) * Constant.PAGE_SIZE_PAGE
                setState { copy(currentPageNumber = firstPage, activeChapterId = resolvedChapterId) }
                emitEvent(ReadBookEvent.GoToChapter(firstPage))
                pagingParams.value = PagingParams(bookId, totalPages, initialKey)
                markReadingSessionStarted()
            }.onError { e ->
                emitError(e.message)
            }
            setLoading(false)
        }
    }

    private fun updateActiveChapter(pageNumber: Int) {
        val chapters = uiState.value.chapters
        if (chapters.isEmpty()) return
        var accumulatedPages = 0
        var foundChapter: Chapter? = null

        for (chapter in chapters) {
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

    fun onPageAudioAvailable(audio: Audio?) {
        val url = audio?.fileUrl
        setState { copy(currentAudioUrl = url, currentTime = 0L, audioDuration = 0L) }
        if (uiState.value.readMode == ReadMode.ListenMode && url != null) {
            emitEvent(ReadBookEvent.PlayAudio(url))
        }
    }

    fun updatePlaybackState(isPlaying: Boolean, currentTime: Long, duration: Long) {
        setState { copy(isPlaying = isPlaying, currentTime = currentTime, audioDuration = duration) }
    }

    fun updatePlaybackSpeed(speed: Float) {
        setState { copy(playbackSpeed = speed) }
    }

    fun goToChapter(chapterId: Int) {
        val state = uiState.value
        val chapters = state.chapters
        if (chapters.isEmpty()) return
        val bookId = state.book?.id ?: return
        val totalPages = state.totalPages

        val firstPageOfChapter = calculateFirstPage(chapterId, chapters)
        val initialKey = (firstPageOfChapter / Constant.PAGE_SIZE_PAGE) * Constant.PAGE_SIZE_PAGE

        setState { copy(currentPageNumber = firstPageOfChapter, activeChapterId = chapterId) }
        emitEvent(ReadBookEvent.GoToChapter(firstPageOfChapter))
        pagingParams.value = PagingParams(bookId, totalPages, initialKey)
    }

    private fun calculateFirstPage(chapterId: Int, sortedChapters: List<Chapter>): Int {
        var firstPage = 0
        for (chapter in sortedChapters) {
            if (chapter.id == chapterId) break
            firstPage += chapter.totalPages
        }
        return firstPage
    }

    fun setReadMode(readMode: Int) {
        setState { copy(readMode = ReadMode.fromValue(readMode)) }
    }

    fun readModeClicked() {
        val currentReadValue = uiState.value.readMode.value
        val newMode = ReadMode.fromValue(1 - currentReadValue)
        setState { copy(readMode = newMode) }

        if (newMode == ReadMode.ReadMode) {
            emitEvent(ReadBookEvent.StopAudio)
        } else {
            uiState.value.currentAudioUrl?.let { url ->
                emitEvent(ReadBookEvent.PlayAudio(url))
            }
        }
    }

    fun onTextSelected(selectedText: String) {
        val normalizedText = normalizeSelectedText(selectedText)
        if (normalizedText.isBlank()) return
        lookupText(normalizedText)
    }

    fun retryLookup() {
        val query = uiState.value.lookupQuery
        if (query.isNotBlank()) {
            lookupText(query)
        }
    }

    fun dismissLookupDialog() {
        setState {
            copy(
                lookupStatus = LookupStatus.Idle,
                lookupResult = null,
                lookupError = null
            )
        }
    }

    private fun lookupText(text: String) {
        viewModelScope.launch(exceptionHandler) {
            setState {
                copy(
                    lookupStatus = LookupStatus.Loading,
                    lookupQuery = text,
                    lookupResult = null,
                    lookupError = null
                )
            }

            val result = lookupTextUseCase(text)
            result.onSuccess { data ->
                setState {
                    copy(
                        lookupStatus = LookupStatus.Success,
                        lookupResult = data,
                        lookupError = null
                    )
                }
            }.onError { error ->
                setState {
                    copy(
                        lookupStatus = LookupStatus.Error,
                        lookupResult = null,
                        lookupError = error.message
                    )
                }
            }
        }
    }

    private fun normalizeSelectedText(rawText: String): String {
        return rawText.trim()
            .replace("\\s+".toRegex(), " ")
            .trim('\"', '\'', ',', '.', ';', ':', '!', '?', '(', ')', '[', ']', '{', '}')
    }

    fun markReadingSessionStarted() {
        val s = uiState.value
        if (s.book == null || s.totalPages <= 0) return
        readingSessionStartElapsed = SystemClock.elapsedRealtime()
    }

    /**
     * Gọi khi rời màn đọc: cập nhật tiến độ + (nếu ≥ ~15s) gửi thời lượng để server ghi LESSON.
     */
    fun reportReadingProgressOnLeave() {
        val start = readingSessionStartElapsed ?: return
        readingSessionStartElapsed = null
        val s = uiState.value
        val book = s.book ?: return
        val total = s.totalPages
        if (total <= 0) return
        val currentPage = s.currentPageNumber
        val durationSec = ((SystemClock.elapsedRealtime() - start) / 1000).toInt()
        if (durationSec < 15) return
        viewModelScope.launch(exceptionHandler) {
            updateBookReadingProgressUseCase(
                bookId = book.id,
                lastReadPageNumber = currentPage,
                totalPages = total,
                durationSeconds = durationSec
            )
        }
    }
}
