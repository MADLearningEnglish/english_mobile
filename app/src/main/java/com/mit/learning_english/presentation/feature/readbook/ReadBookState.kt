package com.mit.learning_english.presentation.feature.readbook

import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.model.Chapter

data class ReadBookState(
    val isPlaying: Boolean = false,
    val currentTime: Int = 0,
    val currentAudio: String? = null,
    val readMode: ReadMode = ReadMode.ReadMode,
    val currentPageNumber: Int = 0,
    val totalPages: Int = 0,
    val chapters: List<Chapter> = emptyList(),
    val activeChapterId: Int? = null,
    val book: BookDetail? = null,
)
