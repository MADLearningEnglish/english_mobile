package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.CreateDeckRequestDto
import com.mit.learning_english.data.remote.dto.DeckDto
import com.mit.learning_english.data.remote.dto.FlashcardDto
import com.mit.learning_english.data.remote.dto.DeckStudyCompleteRequestDto
import com.mit.learning_english.data.remote.dto.StudyResultDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface giao tiếp với RESTful API của Backend Spring Boot liên quan đến Bộ thẻ (Deck) và Flashcard.
 * Sử dụng thư viện Retrofit2. Các endpoint yêu cầu xác thực sẽ tự động được thêm Header nhờ Interceptor.
 */
interface DeckApiService {
    /**
     * Gửi yêu cầu tạo mới một bộ thẻ kèm theo danh sách flashcard (nếu có).
     * @param request Payload chứa tiêu đề và danh sách từ vựng.
     * @return Kết quả trả về chứa thông tin bộ thẻ vừa được tạo.
     */
    @POST("/api/deck/v1")
    suspend fun createDeck(@Body request: CreateDeckRequestDto): Response<BaseResponse<DeckDto>>

    /**
     * Lấy danh sách toàn bộ các bộ thẻ của người dùng hiện tại đang đăng nhập.
     * @param search Từ khóa tìm kiếm tùy chọn (lọc theo tên bộ thẻ).
     * @return Danh sách bộ thẻ (dạng List).
     */
    @GET("/api/deck/v1")
    suspend fun getAllDecks(@Query("search") search: String? = null): Response<BaseResponse<List<DeckDto>>>

    /**
     * Lấy thông tin chi tiết một bộ thẻ dựa vào ID.
     * @param deckId ID của bộ thẻ cần xem.
     * @return Chi tiết bộ thẻ cùng với danh sách thẻ.
     */
    @GET("/api/deck/v1/{id}")
    suspend fun getDeckById(@Path("id") deckId: Int): Response<BaseResponse<DeckDto>>

    /**
     * Cập nhật thông tin tiêu đề và quản lý (thêm/sửa/xoá) các thẻ từ vựng bên trong một bộ thẻ.
     * @param deckId ID của bộ thẻ cần cập nhật.
     * @param request Payload chứa dữ liệu mới nhất của bộ thẻ.
     * @return Phản hồi xác nhận bộ thẻ đã cập nhật thành công.
     */
    @PUT("/api/deck/v1/{id}")
    suspend fun updateDeck(
        @Path("id") deckId: Int,
        @Body request: com.mit.learning_english.data.remote.dto.UpdateDeckRequestDto
    ): Response<BaseResponse<DeckDto>>

    /**
     * Lấy danh sách flashcard cần ôn tập (dựa theo thuật toán lặp lại ngắt quãng - Spaced Repetition nếu có).
     * @param deckId ID của bộ thẻ.
     * @return Danh sách các thẻ cần học hôm nay.
     */
    @GET("/api/deck/v1/{id}/study")
    suspend fun getFlashcardsToStudy(@Path("id") deckId: Int): Response<BaseResponse<List<FlashcardDto>>>

    /**
     * Lấy toàn bộ danh sách flashcards trong một bộ thẻ (không lọc) để học theo kiểu lật bài ngẫu nhiên (Quizlet-style).
     * @param deckId ID của bộ thẻ.
     * @return Danh sách toàn bộ flashcards.
     */
    @GET("/api/deck/v1/{id}/flashcards")
    suspend fun getAllFlashcards(@Path("id") deckId: Int): Response<BaseResponse<List<FlashcardDto>>>

    /**
     * Gửi kết quả ôn tập của TỪNG thẻ đơn lẻ (ví dụ: đánh giá EASY, HARD) lên máy chủ.
     * @param deckId ID bộ thẻ chứa thẻ đó.
     * @param flashcardId ID của thẻ vừa ôn tập.
     * @param level Mức độ nhớ từ. Giá trị hợp lệ: HARD, MEDIUM, EASY, KNOWN.
     * @return Xác nhận thành công.
     */
    @POST("/api/deck/v1/{id}/flashcards/{flashcardId}/review")
    suspend fun reviewFlashcard(
        @Path("id") deckId: Int,
        @Path("flashcardId") flashcardId: Int,
        @Query("level") level: String
    ): Response<BaseResponse<String>>

    /**
     * Lấy kết quả học tập / thống kê của bộ thẻ này (nếu có tính năng hiển thị biểu đồ).
     * @param deckId ID của bộ thẻ.
     * @return Object chứa các thông tin thống kê.
     */
    @GET("/api/deck/v1/{id}/results")
    suspend fun getStudyResults(@Path("id") deckId: Int): Response<BaseResponse<StudyResultDto>>

    /**
     * Thực hiện xóa mềm một bộ thẻ. 
     * @param deckId ID của bộ thẻ cần xóa.
     * @return Thông báo xóa thành công.
     */
    @DELETE("/api/deck/v1/{id}")
    suspend fun deleteDeck(@Path("id") deckId: Int): Response<BaseResponse<String>>

    /**
     * Báo cáo kết thúc toàn bộ một phiên học, bao gồm tổng thời gian học, số thẻ đã ôn và kết quả bài trắc nghiệm.
     * @param deckId ID của bộ thẻ.
     * @param body Payload ghi nhận thời lượng học (`durationSeconds`), số thẻ đã ôn (`cardsReviewed`),
     * số câu trả lời đúng (`quizCorrect`) và tổng số câu hỏi trắc nghiệm (`quizTotal`).
     * @return Thông báo thành công.
     */
    @POST("/api/deck/v1/{id}/study-complete")
    suspend fun postStudyComplete(
        @Path("id") deckId: Int,
        @Body body: DeckStudyCompleteRequestDto
    ): Response<BaseResponse<String>>
}