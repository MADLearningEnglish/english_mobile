package com.mit.learning_english.domain.usecase.level

import com.mit.learning_english.domain.model.LearningLevel
import com.mit.learning_english.domain.repository.LevelRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class GetLevelsUseCase @Inject constructor(
    private val levelRepository: LevelRepository
) {
    suspend operator fun invoke(): Result<List<LearningLevel>> {
        return levelRepository.getLevels()
    }
}
