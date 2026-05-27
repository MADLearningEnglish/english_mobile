package com.mit.learning_english.presentation.feature.readbook

/**
 * Các sự kiện tương tác giữa ViewModel và màn hình đọc sách.
 */
sealed class ReadBookEvent {
    /** Yêu cầu chia sẻ sách (để mở rộng trong tương lai). */
    object ShareBook : ReadBookEvent()
    /** Yêu cầu chuyển tới trang/chương tương ứng theo chỉ số. */
    data class GoToChapter(val index: Int) : ReadBookEvent()
    /** Yêu cầu phát audio của trang hiện tại. */
    data class PlayAudio(val url: String) : ReadBookEvent()
    /** Yêu cầu dừng audio đang phát. */
    object StopAudio : ReadBookEvent()
}