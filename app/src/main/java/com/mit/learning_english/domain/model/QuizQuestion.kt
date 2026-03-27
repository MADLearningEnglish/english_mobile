package com.mit.learning_english.domain.model

/**
 * Các kiểu câu hỏi quiz được hỗ trợ trong phiên học.
 */
enum class QuizType {
    /** Hiện nghĩa tiếng Việt → chọn từ tiếng Anh đúng */
    MEANING_TO_WORD,

    /** Hiện từ tiếng Anh → chọn nghĩa tiếng Việt đúng */
    WORD_TO_MEANING,

    /** Hiện câu ví dụ có ô trống → nhập từ đúng */
    FILL_BLANK
}

/**
 * Một câu hỏi quiz được sinh ra từ dữ liệu flashcard đã tải.
 *
 * @param type          Kiểu câu hỏi
 * @param prompt        Nội dung câu hỏi hiển thị cho người dùng
 * @param correctAnswer Đáp án đúng
 * @param choices       4 đáp án (bao gồm đáp án đúng, đã trộn ngẫu nhiên). Trống với FILL_BLANK.
 * @param sourceFlashcardId  ID thẻ đang kiểm tra
 */
data class QuizQuestion(
    val type: QuizType,
    val prompt: String,
    val correctAnswer: String,
    val choices: List<String>,
    val sourceFlashcardId: Int
)
