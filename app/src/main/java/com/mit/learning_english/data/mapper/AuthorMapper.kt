package com.mit.learning_english.data.mapper

import com.mit.learning_english.data.remote.dto.AuthorResponse
import com.mit.learning_english.domain.model.Author

fun AuthorResponse.toAuthor(): Author {
    return Author(
        id = id,
        name = name,
        avatar = avatar,
        nationality = nationality,
        biography = biography
    )
}
