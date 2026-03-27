package com.mit.learning_english.data.mapper

import com.mit.learning_english.data.remote.dto.BookResponse
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.model.BookReponse
import java.time.LocalDateTime

fun BookResponse.toBookDetail(): BookDetail {
    return BookDetail(
        id = id,
        title = title,
        language = language,
        coverUrl = coverUrl,
        genresName = genresName,
        authorsName = authorsName,
        chapters = chapters ?: emptyList(),
        lastReadNumberPage = lastReadNumberPage
    )
}

fun BookResponse.toBook(): Book {
    return Book(
        id = id,
        title = title,
        language = language,
        coverUrl = coverUrl,
        genresName = genresName,
        authorsName = authorsName
    )
}

fun BookResponse.toBookHistory(): BookReponse {
    return BookReponse(
        id = id,
        title = title,
        language = language,
        coverUrl = coverUrl,
        genresName = genresName,
        authorsName = authorsName,
        processPercent = progressPercent,
        pageLastRead = lastReadNumberPage,
        lastRead = lastReadTime ?: LocalDateTime.MIN,
        isFavorite = isFavorite
    )
}