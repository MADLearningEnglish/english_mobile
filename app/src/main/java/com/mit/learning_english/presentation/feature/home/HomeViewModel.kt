package com.mit.learning_english.presentation.feature.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookReponse
import com.mit.learning_english.domain.model.Author
import com.mit.learning_english.domain.usecase.book.GetAuthorsUseCase
import com.mit.learning_english.domain.usecase.book.GetBookRecommendUseCase
import com.mit.learning_english.domain.usecase.book.GetFavoriteBooksPagingUseCase
import com.mit.learning_english.domain.usecase.book.GetRecentlyReadBookUseCase
import com.mit.learning_english.domain.usecase.genre.GetGenresUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.presentation.feature.historyreadbook.HistoryReadBookEvent
import com.mit.learning_english.shared.FavoriteChangeNotifier
import com.mit.learning_english.shared.UiErrorKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel chịu trách nhiệm xử lý nghiệp vụ cho màn hình chính (HomeFragment).
 * Quản lý và cung cấp dữ liệu sách đề xuất, thể loại, tác giả nổi bật, sách đọc gần đây, sách yêu thích
 * cùng các luồng sự kiện điều hướng đi các màn hình khác.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBookRecommendUseCase: GetBookRecommendUseCase,
    getAuthorsUseCase: GetAuthorsUseCase,
    private val getFavoriteBooksPagingUseCase: GetFavoriteBooksPagingUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    getRecentlyReadBookUseCase: GetRecentlyReadBookUseCase,
    favoriteChangeNotifier: FavoriteChangeNotifier
) : BaseViewModel<HomeState, HomeEvent>(HomeState()) {
    
    /**
     * Luồng PagingData chứa danh sách các cuốn sách vừa mới đọc gần đây.
     */
    val recentBooks: Flow<PagingData<BookReponse>> =
        getRecentlyReadBookUseCase().cachedIn(viewModelScope)
        
    /**
     * Luồng PagingData chứa danh sách các tác giả.
     */
    val authors = getAuthorsUseCase().cachedIn(viewModelScope)

    private val _refreshFavorites = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    
    /**
     * Luồng PagingData chứa danh sách các cuốn sách yêu thích của người dùng, hỗ trợ tự động tải lại khi có sự thay đổi.
     */
    val favoriteBooks: Flow<PagingData<Book>> = _refreshFavorites
        .onStart { emit(Unit) }
        .flatMapLatest { getFavoriteBooksPagingUseCase() }
        .cachedIn(viewModelScope)

    init {
        fetchRecommendBooks()
        fetchGenres()
        viewModelScope.launch {
            favoriteChangeNotifier.favoriteChanged.collect {
                _refreshFavorites.tryEmit(Unit)
            }
        }
    }

    /**
     * Lấy danh sách sách đề xuất từ server/database.
     */
    fun fetchRecommendBooks() {
        viewModelScope.launch(exceptionHandler) {
            setState { copy(isRecommendBooksLoading = true) }
            val result = getBookRecommendUseCase.invoke()
            result.onSuccess { data ->
                setState { copy(recommendBooks = data) }
            }.onLoading {
                setState { copy(isRecommendBooksLoading = true) }
            }.onError { error ->
                emitError(error.message)
            }
            setState { copy(isRecommendBooksLoading = false) }
        }
    }

    /**
     * Lấy danh sách các thể loại sách hiện có.
     */
    fun fetchGenres() {
        viewModelScope.launch(exceptionHandler) {
            setState { copy(isGenresLoading = true) }
            val result = getGenresUseCase.invoke()
            result.onSuccess { data ->
                setState { copy(genres = data) }
            }.onLoading {
                setState { copy(isGenresLoading = true) }
            }.onError { error ->
                emitError(error.message ?: UiErrorKey.UNKNOWN)
            }
            setState { copy(isGenresLoading = false) }
        }
    }

    /**
     * Phát sự kiện điều hướng sang màn hình Tìm kiếm sách.
     */
    fun navigateToSearchBook() {
        emitEvent(HomeEvent.NavigateToSearchFragment)
    }

    /**
     * Phát sự kiện điều hướng sang màn hình Chi tiết sách.
     */
    fun navigateToBookDetail(bookId: Int) {
        emitEvent(HomeEvent.NavigateToBookDetailFragment(bookId))
    }

    /**
     * Phát sự kiện điều hướng sang màn hình Danh sách sách theo thể loại.
     */
    fun navigateToBookByGenre(genreId: Int, genreName: String) {
        emitEvent(HomeEvent.NavigateToBookByGenre(genreId, genreName))
    }

    /**
     * Phát sự kiện điều hướng sang màn hình Danh sách sách đề xuất.
     */
    fun navigateToRecommendBooks() {
        emitEvent(HomeEvent.NavigateToRecommentBookFragment)
    }

    /**
     * Phát sự kiện điều hướng sang màn hình Chi tiết tác giả.
     */
    fun navigateToDetailAuthor(author: Author) {
        emitEvent(
            HomeEvent.NavigateToDetailAuthorFragment(
                authorId = author.id,
                authorName = author.name,
                authorAvatar = author.avatar,
                authorNationality = author.nationality,
                authorBiography = author.biography
            )
        )
    }

    /**
     * Phát thông báo lỗi hiển thị lên giao diện người dùng.
     */
    fun setErrorMessage(message: String) {
        emitError(message)
    }

    /**
     * Phát sự kiện điều hướng sang màn hình Lịch sử đọc sách.
     */
    fun navigateToHistoryReadBooks() {
        emitEvent(
            HomeEvent.NavigateToHistoryReadBooks
        )
    }
}
