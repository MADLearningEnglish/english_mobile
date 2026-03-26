package com.mit.learning_english.presentation.feature.home

sealed class HomeEvent {
    object NavigateToSearchFragment : HomeEvent()
    object NavigateToRecommentBookFragment : HomeEvent()
    object NavigateToRecentlyReadBook : HomeEvent()
}