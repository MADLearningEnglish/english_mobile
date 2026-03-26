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

@HiltViewModel
class CreateDeckViewModel @Inject constructor(
    private val createDeckUseCase: CreateDeckUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel<CreateDeckState, CreateDeckEvent>(CreateDeckState()) {

    fun onTitleChanged(title: String) {
        setState { copy(title = title) }
    }

    fun onDescriptionChanged(desc: String) {
        setState { copy(description = desc) }
    }

    fun onCoverImageSelected(uri: android.net.Uri) {
        setState { copy(coverImageUri = uri) }
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

    fun updateWord(index: Int, value: String) = updateField(index) { copy(word = value) }
    fun updatePhonetic(index: Int, value: String) = updateField(index) { copy(phonetic = value) }
    fun updateMeaning(index: Int, value: String) = updateField(index) { copy(meaning = value) }
    fun updateExample(index: Int, value: String) = updateField(index) { copy(exampleSentence = value) }
    fun updateVisualCueUri(index: Int, uri: android.net.Uri) = updateField(index) { copy(visualCueUri = uri) }

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
        val validCards = state.flashcards.filter { it.word.isNotBlank() && it.meaning.isNotBlank() }
        if (validCards.isEmpty()) {
            emitEvent(CreateDeckEvent.ShowSnackbar("Vui lòng thêm ít nhất 1 từ hợp lệ"))
            return
        }
        viewModelScope.launch(exceptionHandler) {
            setState { copy(isSaving = true, isUploadingImages = true) }
            
            // Upload cover image
            var finalCoverUrl: String? = null
            if (state.coverImageUri != null) {
                when (val result = uploadFileUseCase(state.coverImageUri, context)) {
                    is Result.Success -> finalCoverUrl = result.data.url
                    is Result.Error -> {
                        setState { copy(isSaving = false, isUploadingImages = false) }
                        emitEvent(CreateDeckEvent.ShowSnackbar("Lỗi tải ảnh cover: ${result.message}"))
                        return@launch
                    }
                    else -> Unit
                }
            }

            // Upload flashcard images
            val uploadedCards = validCards.map { card ->
                if (card.visualCueUri != null) {
                    when (val result = uploadFileUseCase(card.visualCueUri, context)) {
                        is Result.Success -> card.copy(visualCueUrl = result.data.url)
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
                coverImageUrl = finalCoverUrl,
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
