package com.mit.learning_english.presentation.feature.createdeck

sealed class CreateDeckEvent {
    object NavigateBack : CreateDeckEvent()
    data class ShowSuccessDialog(val deckId: Int) : CreateDeckEvent()
    data class ShowSnackbar(val message: String) : CreateDeckEvent()
}
