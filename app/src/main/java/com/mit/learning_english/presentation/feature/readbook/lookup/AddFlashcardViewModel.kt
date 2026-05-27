package com.mit.learning_english.presentation.feature.readbook.lookup

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.model.FlashcardUpdateInput
import com.mit.learning_english.domain.model.UpdateDeckRequest
import com.mit.learning_english.domain.usecase.deck.GetDeckByIdUseCase
import com.mit.learning_english.domain.usecase.deck.UpdateDeckUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.shared.UiErrorKey
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * AddFlashcardViewModel – ViewModel xử lý việc thêm flashcard mới vào một deck.
 *
 * Được dùng bởi [AddFlashcardBottomSheet] – bước cuối cùng trong luồng tra cứu từ:
 * ReadBookFragment → ChooseDeckBottomSheet → AddFlashcardBottomSheet → AddFlashcardViewModel
 *
 * Chiến lược "append" flashcard:
 * Backend không có API "thêm 1 thẻ" riêng. Thay vào đó, client phải:
 * 1. Lấy toàn bộ flashcard hiện có của deck qua [GetDeckByIdUseCase].
 * 2. Tạo [UpdateDeckRequest] chứa list cũ + thẻ mới.
 * 3. Gọi [UpdateDeckUseCase] → API `PUT /api/deck/v1/{id}` để ghi đè toàn bộ.
 *
 * State: [AddFlashcardUiState] (isSubmitting, isSuccess, errorMessage)
 */
@HiltViewModel
class AddFlashcardViewModel @Inject constructor(
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val updateDeckUseCase: UpdateDeckUseCase
) : BaseViewModel<AddFlashcardUiState, Unit>(AddFlashcardUiState()) {

    /**
     * Xoá thông báo lỗi khỏi state (gọi sau khi UI đã hiển thị lỗi).
     */
    fun consumeError() {
        setState { copy(errorMessage = null) }
    }

    /**
     * Thêm flashcard mới vào deck được chỉ định.
     *
     * Các bước thực hiện:
     * 1. Trim và validate [term] và [definition] không được rỗng.
     * 2. Gọi [GetDeckByIdUseCase] lấy thông tin deck hiện tại cùng toàn bộ flashcard.
     * 3. Map list flashcard cũ thành [FlashcardUpdateInput] (giữ nguyên id, term, definition).
     * 4. Tạo [UpdateDeckRequest] với list = cũ + thẻ mới (không có id vì là thẻ mới).
     * 5. Gọi [UpdateDeckUseCase] → `PUT /api/deck/v1/{deckId}` để cập nhật deck.
     * 6. Thành công → set [AddFlashcardUiState.isSuccess] = true (BottomSheet đóng lại).
     *
     * @param deckId     ID của deck muốn thêm flashcard vào
     * @param term       Mặt trước của thẻ – thường là từ/cụm từ tiếng Anh vừa tra
     * @param definition Mặt sau của thẻ – nghĩa tiếng Việt từ kết quả AI lookup
     */
    fun submit(deckId: Int, term: String, definition: String) {
        val normalizedTerm = term.trim()
        val normalizedDefinition = definition.trim()
        if (normalizedTerm.isBlank() || normalizedDefinition.isBlank()) {
            setState { copy(errorMessage = UiErrorKey.FILL_TERM_DEFINITION) }
            return
        }

        viewModelScope.launch(exceptionHandler) {
            setState { copy(isSubmitting = true, errorMessage = null) }
            getDeckByIdUseCase(deckId)
                .onSuccess { deck ->
                    // Map thẻ cũ → FlashcardUpdateInput để giữ nguyên id (update, không insert mới)
                    val existingInputs = deck.flashcards.map { card ->
                        FlashcardUpdateInput(
                            id = card.id,
                            term = card.term,
                            definition = card.definition,
                            imageUrl = card.imageUrl,
                            status = 1
                        )
                    }
                    // Tạo request gồm danh sách cũ + thẻ mới (không có id → backend sẽ INSERT)
                    val request = UpdateDeckRequest(
                        title = deck.title,
                        status = deck.status,
                        flashcards = existingInputs + FlashcardUpdateInput(
                            term = normalizedTerm,
                            definition = normalizedDefinition,
                            status = 1
                        )
                    )
                    updateDeckUseCase(deckId, request)
                        .onSuccess {
                            setState { copy(isSubmitting = false, isSuccess = true, errorMessage = null) }
                        }
                        .onError { error ->
                            setState { copy(isSubmitting = false, errorMessage = error.message) }
                        }
                }
                .onError { error ->
                    setState { copy(isSubmitting = false, errorMessage = error.message) }
                }
        }
    }
}

/**
 * UI State cho màn hình thêm flashcard.
 *
 * @param isSubmitting Đang gọi API cập nhật deck (hiển thị loading trên nút "Thêm")
 * @param isSuccess    Thêm thành công → BottomSheet tự đóng và hiển thị Toast
 * @param errorMessage Thông báo lỗi từ server hoặc validation (null nếu không có lỗi)
 */
data class AddFlashcardUiState(
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
