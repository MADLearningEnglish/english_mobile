package com.mit.learning_english.data.mapper

import com.mit.learning_english.data.remote.dto.BookResponse
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.model.BookReponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun BookResponse.toBookDetail(): BookDetail {
    return BookDetail(
        id = id,
        title = title,
        language = language,
        coverUrl = coverUrl,
        genresName = genresName,
        authorsName = authorsName,
        progressPercent = progressPercent,
        chapters = chapters ?: emptyList(),
        lastReadNumberPage = lastReadNumberPage,
        isFavorite = isFavorite
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
        lastRead = lastReadTime?.parseToLocalDateTime() ?: LocalDateTime.MIN,
        isFavorite = isFavorite
    )
}

private fun String.parseToLocalDateTime(): LocalDateTime? {
    return try {
        LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: DateTimeParseException) {
        null
    }
}