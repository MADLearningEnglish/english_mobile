package com.mit.learning_english.presentation.feature.home

sealed class HomeEvent {
    object NavigateToSearchFragment : HomeEvent()
    data class NavigateToBookByGenre(val genreId: Int, val genreName: String) : HomeEvent()
    data class NavigateToBookDetailFragment(val bookId: Int) : HomeEvent()
    data class NavigateToDetailAuthorFragment(
        val authorId: Int,
        val authorName: String,
        val authorAvatar: String,
        val authorNationality: String,
        val authorBiography: String
    ) : HomeEvent()
    object NavigateToRecommentBookFragment : HomeEvent()
    object NavigateToHistoryReadBooks : HomeEvent()
}