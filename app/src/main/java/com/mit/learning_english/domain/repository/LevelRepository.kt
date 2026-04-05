package com.mit.learning_english.domain.repository

import com.mit.learning_english.domain.model.LearningLevel
import com.mit.learning_english.domain.util.Result

interface LevelRepository {
    suspend fun getLevels(): Result<List<LearningLevel>>
}
