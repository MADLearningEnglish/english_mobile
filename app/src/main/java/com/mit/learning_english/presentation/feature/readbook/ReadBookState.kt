package com.mit.learning_english.presentation.feature.readbook

import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.model.Chapter

data class ReadBookState(
    val isPlaying: Boolean = false,
    val currentTime: Long = 0L,
    val audioDuration: Long = 0L,
    val currentAudioUrl: String? = null,
    val playbackSpeed: Float = 1.0f,
    val readMode: ReadMode = ReadMode.ReadMode,
    val currentPageNumber: Int = 0,
    val totalPages: Int = 0,
    val chapters: List<Chapter> = emptyList(),
    val activeChapterId: Int? = null,
    val book: BookDetail? = null,
)
