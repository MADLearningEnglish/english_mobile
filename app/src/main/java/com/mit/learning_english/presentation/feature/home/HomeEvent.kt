package com.mit.learning_english.presentation.feature.home

/**
 * Các sự kiện điều hướng (Navigation Events) từ màn hình chính (HomeFragment) sang các màn hình khác.
 */
sealed class HomeEvent {
    /**
     * Điều hướng sang màn hình Tìm kiếm sách (SearchBookFragment).
     */
    object NavigateToSearchFragment : HomeEvent()

    /**
     * Điều hướng sang màn hình hiển thị danh sách sách theo thể loại (BooksByGenreFragment).
     */
    data class NavigateToBookByGenre(val genreId: Int, val genreName: String) : HomeEvent()

    /**
     * Điều hướng sang màn hình Chi tiết sách (BookDetailFragment).
     */
    data class NavigateToBookDetailFragment(val bookId: Int) : HomeEvent()

    /**
     * Điều hướng sang màn hình Chi tiết tác giả (DetailAuthorFragment).
     */
    data class NavigateToDetailAuthorFragment(
        val authorId: Int,
        val authorName: String,
        val authorAvatar: String,
        val authorNationality: String,
        val authorBiography: String
    ) : HomeEvent()

    /**
     * Điều hướng sang màn hình danh sách sách gợi ý/đề xuất (RecommendBookFragment).
     */
    object NavigateToRecommentBookFragment : HomeEvent()

    /**
     * Điều hướng sang màn hình lịch sử đọc sách (HistoryReadBookFragment).
     */
    object NavigateToHistoryReadBooks : HomeEvent()
}