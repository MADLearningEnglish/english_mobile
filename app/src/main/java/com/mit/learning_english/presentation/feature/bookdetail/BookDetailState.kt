package com.mit.learning_english.presentation.feature.bookdetail

import com.mit.learning_english.domain.model.Chapter

/**
 * Trạng thái giao diện (UI State) của màn hình Chi tiết sách.
 *
 * @property id ID của cuốn sách.
 * @property title Tiêu đề cuốn sách.
 * @property language Ngôn ngữ cuốn sách.
 * @property coverUrl URL ảnh bìa cuốn sách.
 * @property genresName Tên các thể loại của sách (cách nhau bởi dấu phẩy).
 * @property authorsName Tên các tác giả của sách (cách nhau bởi dấu phẩy).
 * @property progressPercent Phần trăm tiến độ đọc cuốn sách của người dùng.
 * @property chapters Danh sách các chương (chapters) của cuốn sách.
 * @property lastReadNumberPage Vị trí trang sách đã đọc cuối cùng của người dùng.
 * @property isFavorite Trạng thái sách yêu thích (đã được thêm vào danh sách yêu thích hay chưa).
 */
data class BookDetailState(
    val id: Int = 0,
    val title: String = "",
    val language: String = "",
    val coverUrl: String = "",
    val genresName: String = "",
    val authorsName: String = "",
    val progressPercent: Double = 0.0,
    val chapters: List<Chapter> = emptyList(),
    val lastReadNumberPage: Int = 0,
    val isFavorite: Boolean = false,
)
