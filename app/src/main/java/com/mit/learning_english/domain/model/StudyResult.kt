package com.mit.learning_english.domain.model

data class StudyResult(
    val totalWords: Int,
    val masteryPercentage: Double,
    val knownCount: Long,
    val easyCount: Long,
    val mediumCount: Long,
    val hardCount: Long
)