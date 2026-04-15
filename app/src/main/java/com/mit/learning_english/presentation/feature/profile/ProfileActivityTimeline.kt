package com.mit.learning_english.presentation.feature.profile

import com.mit.learning_english.domain.model.profile.LearningActivityItem

object ProfileActivityCategoryKey {
    const val FLASHCARD = "FLASHCARD"
    /** Chỉ các phiên đọc sách (BOOK) trong ngày */
    const val BOOKS_DAY = "BOOKS_DAY"
    /** Chỉ bài tập (EXERCISE) trong ngày */
    const val EXERCISES_DAY = "EXERCISES_DAY"
    const val AI_CHAT = "AI_CHAT"
}

fun LearningActivityItem.timelineCategoryKey(): String? {
    val t = activityType?.uppercase().orEmpty()
    return when {
        t.contains("FLASHCARD") -> ProfileActivityCategoryKey.FLASHCARD
        t.contains("BOOK") -> ProfileActivityCategoryKey.BOOKS_DAY
        t.contains("EXERCISE") -> ProfileActivityCategoryKey.EXERCISES_DAY
        t.contains("AI") -> ProfileActivityCategoryKey.AI_CHAT
        else -> null
    }
}

fun LearningActivityItem.matchesTimelineCategory(category: String): Boolean =
    timelineCategoryKey() == category
