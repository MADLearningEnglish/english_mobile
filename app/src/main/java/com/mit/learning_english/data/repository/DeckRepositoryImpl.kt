package com.mit.learning_english.data.repository

import com.mit.learning_english.data.mapper.toDomain
import com.mit.learning_english.data.mapper.toDto
import com.mit.learning_english.data.remote.dto.DeckStudyCompleteRequestDto
import com.mit.learning_english.data.remote.api.DeckApiService
import com.mit.learning_english.domain.model.*
import com.mit.learning_english.domain.repository.DeckRepository
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.shared.UiErrorKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation của DeckRepository.
 * Chịu trách nhiệm gọi API thông qua DeckApiService và map dữ liệu từ DTO sang Domain Model.
 * Các phương thức đều được thực thi an toàn trên luồng [Dispatchers.IO].
 */
class DeckRepositoryImpl @Inject constructor(
    private val apiService: DeckApiService
) : DeckRepository {

    /**
     * Tạo một bộ thẻ mới.
     * @param request Dữ liệu tạo bộ thẻ từ tầng Presentation.
     * @return Result chứa Deck nếu thành công, hoặc Error nếu thất bại.
     */
    override suspend fun createDeck(request: CreateDeckRequest): Result<Deck> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createDeck(request.toDto())
            if (response.isSuccessful && response.body()?.data != null) {
                Result.Success(response.body()!!.data!!.toDomain())
            } else {
                Result.Error(response.message() ?: UiErrorKey.CREATE_DECK_VI)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: UiErrorKey.UNKNOWN)
        }
    }

    /**
     * Lấy toàn bộ danh sách các bộ thẻ, có hỗ trợ tìm kiếm.
     * @param search Từ khóa tìm kiếm tên bộ thẻ (không bắt buộc).
     * @return Result chứa danh sách các bộ thẻ.
     */
    override suspend fun getAllDecks(search: String?): Result<List<Deck>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllDecks(search)
            if (response.isSuccessful) {
                // Quá trình mapping toDomain() giờ đây đã an toàn trên luồng nền (IO)
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(data)
            } else {
                Result.Error(response.message() ?: "Lỗi tải danh sách bộ thẻ")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: UiErrorKey.UNKNOWN)
        }
    }

    /**
     * Lấy danh sách flashcard cần ôn tập (dựa theo logic của Backend).
     * @param deckId ID của bộ thẻ.
     * @return Result chứa danh sách Flashcard cần học.
     */
    override suspend fun getFlashcardsToStudy(deckId: Int): Result<List<Flashcard>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFlashcardsToStudy(deckId)
            if (response.isSuccessful) {
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(data)
            } else {
                Result.Error("Failed to fetch flashcards")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Lấy tất cả flashcard trong bộ thẻ (không phân biệt đã học hay chưa).
     * Thường dùng cho chức năng học lật thẻ ngẫu nhiên.
     * @param deckId ID bộ thẻ.
     * @return Result chứa danh sách toàn bộ Flashcard.
     */
    override suspend fun getAllFlashcards(deckId: Int): Result<List<Flashcard>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllFlashcards(deckId)
            if (response.isSuccessful) {
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(data)
            } else {
                Result.Error("Failed to fetch flashcards")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Đánh giá và gửi kết quả ôn tập của một thẻ cụ thể.
     * @param deckId ID bộ thẻ.
     * @param flashcardId ID thẻ.
     * @param level Đánh giá mức độ nhớ từ của người dùng.
     * @return Result.Success nếu ghi nhận thành công.
     */
    override suspend fun reviewFlashcard(deckId: Int, flashcardId: Int, level: MasteryLevel): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.reviewFlashcard(deckId, flashcardId, level.name)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(UiErrorKey.REVIEW_FAILED)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: UiErrorKey.UNKNOWN)
        }
    }

    /**
     * Lấy thông tin thống kê kết quả học tập của bộ thẻ.
     * @param deckId ID bộ thẻ.
     * @return Result chứa dữ liệu thống kê.
     */
    override suspend fun getStudyResults(deckId: Int): Result<StudyResult> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getStudyResults(deckId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.Success(response.body()!!.data!!.toDomain())
            } else {
                Result.Error(UiErrorKey.CANNOT_GET_RESULTS)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: UiErrorKey.UNKNOWN)
        }
    }

    /**
     * Lấy chi tiết thông tin bộ thẻ bằng ID.
     * @param deckId ID bộ thẻ cần lấy.
     * @return Result chứa thông tin chi tiết Deck.
     */
    override suspend fun getDeckById(deckId: Int): Result<Deck> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getDeckById(deckId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.Success(response.body()!!.data!!.toDomain())
            } else {
                Result.Error(response.message() ?: "Lỗi tải thông tin bộ thẻ")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Gửi yêu cầu cập nhật bộ thẻ (thêm/sửa/xóa flashcards bên trong).
     * @param deckId ID bộ thẻ.
     * @param request Dữ liệu thay đổi.
     * @return Result chứa thông tin Deck sau khi cập nhật thành công.
     */
    override suspend fun updateDeck(deckId: Int, request: UpdateDeckRequest): Result<Deck> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateDeck(deckId, request.toDto())
            if (response.isSuccessful && response.body()?.data != null) {
                Result.Success(response.body()!!.data!!.toDomain())
            } else {
                Result.Error(response.message() ?: "Lỗi cập nhật bộ thẻ")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Xóa một bộ thẻ.
     * @param deckId ID bộ thẻ cần xóa.
     * @return Result.Success nếu xóa thành công.
     */
    override suspend fun deleteDeck(deckId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteDeck(deckId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(UiErrorKey.DELETE_FAILED)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: UiErrorKey.UNKNOWN)
        }
    }

    /**
     * Gửi báo cáo hoàn thành phiên học.
     * @param deckId ID bộ thẻ vừa học.
     * @param durationSeconds Tổng thời gian học (tính bằng giây).
     * @param cardsReviewed (Tuỳ chọn) Số lượng thẻ đã ôn tập.
     * @param quizCorrect (Tuỳ chọn) Số câu trắc nghiệm đúng.
     * @param quizTotal (Tuỳ chọn) Tổng số câu trắc nghiệm.
     * @return Result.Success nếu báo cáo thành công.
     */
    override suspend fun postStudyComplete(
        deckId: Int,
        durationSeconds: Int,
        cardsReviewed: Int?,
        quizCorrect: Int?,
        quizTotal: Int?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val body = DeckStudyCompleteRequestDto(
                durationSeconds = durationSeconds,
                cardsReviewed = cardsReviewed,
                quizCorrect = quizCorrect,
                quizTotal = quizTotal
            )
            val response = apiService.postStudyComplete(deckId, body)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message() ?: UiErrorKey.STUDY_COMPLETE_FAILED)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: UiErrorKey.UNKNOWN)
        }
    }
}