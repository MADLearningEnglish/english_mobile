package com.mit.learning_english.presentation.feature.editdeck

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.model.FlashcardUpdateInput
import com.mit.learning_english.domain.model.UpdateDeckRequest
import com.mit.learning_english.domain.usecase.deck.GetDeckByIdUseCase
import com.mit.learning_english.domain.usecase.deck.UpdateDeckUseCase
import com.mit.learning_english.domain.usecase.file.UploadFileUseCase
import com.mit.learning_english.domain.usecase.dictionary.FetchPhoneticUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDeckViewModel @Inject constructor(
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val updateDeckUseCase: UpdateDeckUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : BaseViewModel<EditDeckState, EditDeckEvent>(EditDeckState()) {

    private val navDeckId: Int = savedStateHandle.get<Int>("deckId") ?: -1

    init {
        if (navDeckId != -1) {
            loadDeckData(navDeckId)
        }
    }

    private fun loadDeckData(deckId: Int) {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            setState { copy(deckId = deckId) }
            val result = getDeckByIdUseCase(deckId)
            when (result) {
                is Result.Success -> {
                    setLoading(false)
                    val deck = result.data
                    setState {
                        copy(
                            title = deck.title,
                            status = deck.status
                        )
                    }
                    loadFlashcards(deck)
                }
                is Result.Error -> {
                    setLoading(false)
                    emitEvent(EditDeckEvent.ShowSnackbar(result.message ?: "Lỗi tải dữ liệu"))
                }
                else -> setLoading(false)
            }
        }
    }

    private fun loadFlashcards(deck: com.mit.learning_english.domain.model.Deck) {
        val initialCards = deck.flashcards.map { f ->
            FlashcardUpdateInput(
                id = f.id,
                term = f.term,
                definition = f.definition,
                imageUrl = f.imageUrl,
                status = 1
            )
        }
        setState { copy(flashcards = initialCards) }
    }

    fun onTitleChanged(title: String) {
        setState { copy(title = title) }
    }




    fun toggleExpanded(index: Int) {
        setState {
            copy(expandedIndex = if (expandedIndex == index) -1 else index)
        }
    }

    fun addFlashcard() {
        val current = uiState.value
        if (current.isAtLimit) {
            emitEvent(EditDeckEvent.ShowSnackbar("Đã đạt giới hạn ${current.maxWords} từ"))
            return
        }
        val updated = current.flashcards + FlashcardUpdateInput()
        setState { copy(flashcards = updated, expandedIndex = updated.lastIndex) }
    }

    fun removeFlashcard(index: Int) {
        val current = uiState.value
        val updated = current.flashcards.toMutableList()
        if (index in updated.indices) {
            val card = updated[index]
            if (card.id == null) {
                // Not saved yet, just remove from list
                updated.removeAt(index)
            } else {
                // Saved in DB, mark as deleted
                updated[index] = card.copy(status = 0)
            }
        }
        val newExpanded = if (current.expandedIndex == index) -1 else current.expandedIndex
        setState { copy(flashcards = updated, expandedIndex = newExpanded) }
    }

    fun updateTerm(index: Int, value: String) = updateField(index) { copy(term = value) }
    fun updateDefinition(index: Int, value: String) = updateField(index) { copy(definition = value) }
    fun updateImageUri(index: Int, uri: Uri) = updateField(index) { copy(imageUri = uri) }

    private fun updateField(index: Int, updater: FlashcardUpdateInput.() -> FlashcardUpdateInput) {
        val updated = uiState.value.flashcards.toMutableList()
        if (index in updated.indices) {
            updated[index] = updated[index].updater()
            setState { copy(flashcards = updated) }
        }
    }

    fun saveDeck() {
        val state = uiState.value
        if (state.title.isBlank()) {
            emitEvent(EditDeckEvent.ShowSnackbar("Vui lòng nhập tên bộ thẻ"))
            return
        }
        val validCards = state.flashcards.filter { it.status == 0 || (it.term.isNotBlank() && it.definition.isNotBlank()) }
        if (validCards.filter { it.status != 0 }.isEmpty()) {
            emitEvent(EditDeckEvent.ShowSnackbar("Vui lòng thêm ít nhất 1 từ hợp lệ"))
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
                            emitEvent(EditDeckEvent.ShowSnackbar("Lỗi tải ảnh thẻ: ${result.message}"))
                            return@launch
                        }
                        else -> card
                    }
                } else {
                    card
                }
            }
            
            setState { copy(isUploadingImages = false) }

            val request = UpdateDeckRequest(
                title = state.title.trim(),
                status = state.status,
                flashcards = uploadedCards
            )
            
            when (val result = updateDeckUseCase(state.deckId, request)) {
                is Result.Success -> {
                    setState { copy(isSaving = false) }
                    emitEvent(EditDeckEvent.ShowSuccessDialog(state.deckId))
                }
                is Result.Error -> {
                    setState { copy(isSaving = false) }
                    emitEvent(EditDeckEvent.ShowSnackbar(result.message ?: "Lỗi không xác định"))
                }
                else -> setState { copy(isSaving = false) }
            }
        }
    }
    
    fun onNavigateBack() {
        emitEvent(EditDeckEvent.NavigateBack)
    }
}
