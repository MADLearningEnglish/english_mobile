package com.mit.learning_english.presentation.feature.profile

import com.mit.learning_english.domain.model.profile.LearningActivityItem

object ProfileActivityCategoryKey {
    const val FLASHCARD = "FLASHCARD"
    const val LESSON_AND_EXERCISE = "LESSON_AND_EXERCISE"
    const val AI_CHAT = "AI_CHAT"
}

fun LearningActivityItem.timelineCategoryKey(): String? {
    val t = activityType?.uppercase().orEmpty()
    return when {
        t.contains("FLASHCARD") -> ProfileActivityCategoryKey.FLASHCARD
        t.contains("LESSON") || t.contains("EXERCISE") -> ProfileActivityCategoryKey.LESSON_AND_EXERCISE
        t.contains("AI") -> ProfileActivityCategoryKey.AI_CHAT
        else -> null
    }
}

fun LearningActivityItem.matchesTimelineCategory(category: String): Boolean =
    timelineCategoryKey() == category
