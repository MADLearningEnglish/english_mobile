package com.mit.learning_english.presentation.feature.readbook

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReadBookArgs(
    val bookId: Int, val chapterId: Int? = null, val readModeValue: Int = 0,
) : Parcelable