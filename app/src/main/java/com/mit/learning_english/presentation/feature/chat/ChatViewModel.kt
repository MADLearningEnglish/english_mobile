package com.mit.learning_english.presentation.feature.chat

import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() :
    BaseViewModel<ChatState, ChatEvent>(ChatState()) {}
