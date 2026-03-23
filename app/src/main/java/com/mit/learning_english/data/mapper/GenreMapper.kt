package com.mit.learning_english.data.mapper

import com.mit.learning_english.data.remote.dto.GenreResponse
import com.mit.learning_english.domain.model.Genre

fun GenreResponse.toGenre(): Genre {
    return Genre(
        id = id, name = name, thumbnail = thumbnail, description = description
    )
}