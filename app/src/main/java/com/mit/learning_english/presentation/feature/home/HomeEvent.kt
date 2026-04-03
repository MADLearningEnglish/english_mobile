package com.mit.learning_english.presentation.feature.home

sealed class HomeEvent {
    object NavigateToSearchFragment : HomeEvent()
    data class NavigateToBookByGenre(val genreId: Int, val genreName: String) : HomeEvent()
    data class NavigateToBookDetailFragment(val bookId: Int) : HomeEvent()
    object NavigateToRecommentBookFragment : HomeEvent()
    object NavigateToRecentlyReadBook : HomeEvent()
}