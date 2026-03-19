package com.mit.learning_english.presentation.feature.bookdetail

sealed class BookDetailEvent {
    object NavigateToSearchFragment : BookDetailEvent()
    object NavigateToRecommentBookFragment : BookDetailEvent()
    object NavigateToRecentlyReadBook : BookDetailEvent()

}