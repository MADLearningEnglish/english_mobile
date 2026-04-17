package com.mit.learning_english.presentation.feature.createdeck

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.model.CreateDeckRequest
import com.mit.learning_english.domain.model.FlashcardInput
import com.mit.learning_english.domain.usecase.deck.CreateDeckUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.mit.learning_english.domain.usecase.file.UploadFileUseCase
import com.mit.learning_english.domain.usecase.dictionary.FetchPhoneticUseCase

@HiltViewModel
class CreateDeckViewModel @Inject constructor(
    private val createDeckUseCase: CreateDeckUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel<CreateDeckState, CreateDeckEvent>(CreateDeckState()) {

    fun onTitleChanged(title: String) {
        setState { copy(title = title) }
    }




    fun addFlashcard() {
        val current = uiState.value
        if (current.isAtLimit) {
            emitEvent(CreateDeckEvent.ShowSnackbar("Đã đạt giới hạn ${current.maxWords} từ"))
            return
        }
        val updated = current.flashcards + FlashcardInput()
        setState { copy(flashcards = updated, expandedIndex = updated.lastIndex) }
    }

    fun removeFlashcard(index: Int) {
        val updated = uiState.value.flashcards.toMutableList().also { it.removeAt(index) }
        val newExpanded = when {
            updated.isEmpty() -> -1
            index <= uiState.value.expandedIndex -> (uiState.value.expandedIndex - 1).coerceAtLeast(0)
            else -> uiState.value.expandedIndex
        }
        setState { copy(flashcards = updated, expandedIndex = newExpanded) }
    }

    fun toggleExpanded(index: Int) {
        setState {
            copy(expandedIndex = if (expandedIndex == index) -1 else index)
        }
    }

    fun updateTerm(index: Int, value: String) = updateField(index) { copy(term = value) }
    fun updateDefinition(index: Int, value: String) = updateField(index) { copy(definition = value) }
    fun updateImageUri(index: Int, uri: android.net.Uri) = updateField(index) { copy(imageUri = uri) }

    private fun updateField(index: Int, updater: FlashcardInput.() -> FlashcardInput) {
        val updated = uiState.value.flashcards.toMutableList()
        if (index in updated.indices) {
            updated[index] = updated[index].updater()
            setState { copy(flashcards = updated) }
        }
    }

    fun saveDeck() {
        val state = uiState.value
        if (state.title.isBlank()) {
            emitEvent(CreateDeckEvent.ShowSnackbar("Vui lòng nhập tên bộ thẻ"))
            return
        }
        
        val hasInvalidCards = state.flashcards.any { it.term.isBlank() || it.definition.isBlank() }
        if (hasInvalidCards) {
            emitEvent(CreateDeckEvent.ShowSnackbar("Vui lòng điền đầy đủ Thuật ngữ và Định nghĩa cho tất cả các thẻ"))
            return
        }
        
        val validCards = state.flashcards
        if (validCards.isEmpty()) {
            emitEvent(CreateDeckEvent.ShowSnackbar("Vui lòng thêm ít nhất 1 từ hợp lệ"))
            return
        }
        viewModelScope.launch(exceptionHandler) {
            setState { copy(isSaving = true, isUploadingImages = true) }
            


            // Upload flashcard images
            val uploadedCards = validCards.map { card ->
                if (card.imageUri != null) {
                    when (val result = uploadFileUseCase(card.imageUri, context)) {
                        is Result.Success -> card.copy(imageUrl = result.data.url)
                        is Result.Error -> {
                            setState { copy(isSaving = false, isUploadingImages = false) }
                            emitEvent(CreateDeckEvent.ShowSnackbar("Lỗi tải ảnh thẻ: ${result.message}"))
                            return@launch
                        }
                        else -> card
                    }
                } else {
                    card
                }
            }

            setState { copy(isUploadingImages = false) }

            val request = CreateDeckRequest(
                title = state.title.trim(),
                flashcards = uploadedCards
            )
            when (val result = createDeckUseCase(request)) {
                is Result.Success -> {
                    setState { copy(isSaving = false) }
                    emitEvent(CreateDeckEvent.ShowSuccessDialog(result.data.id))
                }
                is Result.Error -> {
                    setState { copy(isSaving = false) }
                    emitEvent(CreateDeckEvent.ShowSnackbar(result.message ?: "Lỗi không xác định"))
                }
                else -> setState { copy(isSaving = false) }
            }
        }
    }

    fun onNavigateBack() {
        emitEvent(CreateDeckEvent.NavigateBack)
    }
}
