package com.mit.learning_english.data.mapper

import com.mit.learning_english.data.remote.dto.LevelDto
import com.mit.learning_english.domain.model.LearningLevel

fun LevelDto.toLearningLevel(): LearningLevel {
    return LearningLevel(
        id = id,
        name = name.orEmpty(),
        description = description.orEmpty()
    )
}
