package com.mit.learning_english.presentation.feature.editdeck

sealed class EditDeckEvent {
    object NavigateBack : EditDeckEvent()
    data class ShowSuccessDialog(val deckId: Int) : EditDeckEvent()
    data class ShowSnackbar(val message: String) : EditDeckEvent()
}
