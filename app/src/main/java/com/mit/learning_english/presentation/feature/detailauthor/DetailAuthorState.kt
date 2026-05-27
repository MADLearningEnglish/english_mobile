package com.mit.learning_english.presentation.feature.detailauthor

/**
 * Trạng thái giao diện của màn hình chi tiết tác giả.
 */
data class DetailAuthorState(
    val authorName: String = "",
    val authorAvatar: String = "",
    val authorNationality: String = "",
    val authorBiography: String = ""
)
