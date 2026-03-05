package com.mit.learning_english.presentation.feature.vocabulary

import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VocabularyViewModel @Inject constructor() :
    BaseViewModel<VocabularyState, VocabularyEvent>(VocabularyState()) {}
