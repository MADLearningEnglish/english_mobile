package com.mit.learning_english.presentation.feature.signup

sealed class SignUpEvent {
	object NavigateToLogin : SignUpEvent()
}