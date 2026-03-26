package com.mit.learning_english.presentation.feature.bookdetail

import com.mit.learning_english.presentation.feature.readbook.ReadBookArgs

sealed class BookDetailEvent {
    data class NavigateToReadBook(val readBookArgs: ReadBookArgs) : BookDetailEvent()

}