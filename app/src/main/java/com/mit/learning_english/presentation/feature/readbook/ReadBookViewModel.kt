package com.mit.learning_english.presentation.feature.readbook

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.domain.model.Audio
import com.mit.learning_english.domain.model.Chapter
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.usecase.book.GetBookDetailByIdUseCase
import android.os.SystemClock
import android.util.Log
import com.mit.learning_english.domain.usecase.book.UpdateBookReadingProgressUseCase
import com.mit.learning_english.domain.usecase.page.GetPagesByBookUseCase
import com.mit.learning_english.domain.usecase.page.LookupTextUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.shared.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
/**
 * ViewModel điều phối luồng đọc/nghe sách, paging trang và tra từ.
 */
class ReadBookViewModel @Inject constructor(
    private val getBookDetailByIdUseCase: GetBookDetailByIdUseCase,
    private val getPagesByBookUseCase: GetPagesByBookUseCase,
    private val lookupTextUseCase: LookupTextUseCase,
    private val updateBookReadingProgressUseCase: UpdateBookReadingProgressUseCase
) : BaseViewModel<ReadBookState, ReadBookEvent>(ReadBookState()) {

    /**
     * Cấu hình truy vấn paging trang sách.
     */
    private data class PagingParams(val bookId: Int, val totalPages: Int, val initialKey: Int)

    private val pagingParams = MutableStateFlow<PagingParams?>(null)

    /** Thời điểm bắt đầu phiên đọc (foreground), để gửi durationSeconds lên server. */
    private var readingSessionStartElapsed: Long? = null

    /**
     * Luồng phân trang các trang sách theo tham số hiện tại.
     */
    val pagesFlow: Flow<PagingData<Page>> = pagingParams
        .filterNotNull()
        .flatMapLatest { params ->
            getPagesByBookUseCase(params.bookId, params.totalPages, params.initialKey)
        }
        .cachedIn(viewModelScope)

    /**
     * Tải dữ liệu ban đầu cho màn đọc sách từ đối số điều hướng.
     */
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
                Log.d("ReadBookViewModel",firstPage.toString()+chapterId.toString()+bookDetail.lastReadNumberPage)
                pagingParams.value = PagingParams(bookId, totalPages, initialKey)
                markReadingSessionStarted()
            }.onError { e ->
                emitError(e.message)
            }
            setLoading(false)
        }
    }

    /**
     * Đồng bộ chương active theo số trang hiện tại.
     */
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

    /**
     * Cập nhật trang hiện tại khi ViewPager thay đổi.
     */
    fun onPageChanged(position: Int) {
        setState { copy(currentPageNumber = position) }
        updateActiveChapter(position)
    }

    /**
     * Cập nhật audio của trang hiện tại và tự phát nếu đang ở chế độ nghe.
     */
    fun onPageAudioAvailable(audio: Audio?) {
        val url = audio?.fileUrl
        setState { copy(currentAudioUrl = url, currentTime = 0L, audioDuration = 0L) }
        if (uiState.value.readMode == ReadMode.ListenMode && url != null) {
            emitEvent(ReadBookEvent.PlayAudio(url))
        }
    }

    /**
     * Cập nhật trạng thái phát audio để hiển thị lên UI.
     */
    fun updatePlaybackState(isPlaying: Boolean, currentTime: Long, duration: Long) {
        setState { copy(isPlaying = isPlaying, currentTime = currentTime, audioDuration = duration) }
    }

    /**
     * Lưu tốc độ phát audio hiện tại.
     */
    fun updatePlaybackSpeed(speed: Float) {
        setState { copy(playbackSpeed = speed) }
    }

    /**
     * Chuyển tới chương được chọn và cập nhật paging key tương ứng.
     */
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

    /**
     * Tính trang bắt đầu của chương dựa trên danh sách chương đã sắp xếp.
     */
    private fun calculateFirstPage(chapterId: Int, sortedChapters: List<Chapter>): Int {
        var firstPage = 0
        for (chapter in sortedChapters) {
            if (chapter.id == chapterId) break
            firstPage += chapter.totalPages
        }
        return firstPage
    }

    /**
     * Đặt chế độ đọc theo giá trị điều hướng.
     */
    fun setReadMode(readMode: Int) {
        setState { copy(readMode = ReadMode.fromValue(readMode)) }
    }

    /**
     * Đổi qua lại giữa chế độ đọc và nghe.
     */
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

    /**
     * Nhận đoạn text người dùng chọn để thực hiện tra nghĩa.
     */
    fun onTextSelected(selectedText: String) {
        val normalizedText = normalizeSelectedText(selectedText)
        if (normalizedText.isBlank()) return
        lookupText(normalizedText)
    }

    /**
     * Thử tra nghĩa lại với truy vấn gần nhất.
     */
    fun retryLookup() {
        val query = uiState.value.lookupQuery
        if (query.isNotBlank()) {
            lookupText(query)
        }
    }

    /**
     * Đóng hộp thoại tra từ và reset trạng thái lookup.
     */
    fun dismissLookupDialog() {
        setState {
            copy(
                lookupStatus = LookupStatus.Idle,
                lookupResult = null,
                lookupError = null
            )
        }
    }

    /**
     * Gọi use case tra từ và cập nhật trạng thái thành công/lỗi.
     */
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

    /**
     * Chuẩn hóa đoạn text bôi đen trước khi gửi lên API tra từ.
     */
    private fun normalizeSelectedText(rawText: String): String {
        return rawText.trim()
            .replace("\\s+".toRegex(), " ")
            .trim('\"', '\'', ',', '.', ';', ':', '!', '?', '(', ')', '[', ']', '{', '}')
    }

    /**
     * Đánh dấu bắt đầu phiên đọc để tính thời lượng đọc.
     */
    fun markReadingSessionStarted() {
        val s = uiState.value
        if (s.book == null || s.totalPages <= 0) return
        readingSessionStartElapsed = SystemClock.elapsedRealtime()
    }

    /**
     * Gửi tiến độ và thời lượng đọc khi rời màn hình.
     */
    fun reportReadingProgressOnLeave() {
        val start = readingSessionStartElapsed ?: return
        val s = uiState.value
        val bookId = s.book?.id ?: return
        readingSessionStartElapsed = null
        val currentPage = s.currentPageNumber
        val durationSec = ((SystemClock.elapsedRealtime() - start) / 1000).toInt().coerceAtLeast(1)
        viewModelScope.launch(exceptionHandler) {
            // Keep this fire-and-forget update alive while leaving the screen.
            withContext(Dispatchers.IO + NonCancellable) {
                updateBookReadingProgressUseCase(
                    bookId = bookId,
                    lastReadPageNumber = currentPage,
                    lastRead = LocalDateTime.now(),
                    duration = durationSec
                )
            }
        }
    }


}
