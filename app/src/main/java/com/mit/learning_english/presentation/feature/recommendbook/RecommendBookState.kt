package com.mit.learning_english.presentation.feature.recommendbook

import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookReponse

data class RecommendBookState(
    val booksByTopic: List<Book> = emptyList(),
    val booksByAuthor: List<Book> = emptyList(),
    val booksInProgress: List<BookReponse> = emptyList()
)
