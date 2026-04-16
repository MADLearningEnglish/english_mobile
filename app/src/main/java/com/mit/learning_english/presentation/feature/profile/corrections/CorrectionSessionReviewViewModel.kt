package com.mit.learning_english.presentation.feature.profile.corrections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.model.profile.CorrectionSessionReview
import com.mit.learning_english.domain.repository.ProfileRepository
import com.mit.learning_english.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CorrectionSessionReviewViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CorrectionSessionReview?>(null)
    val state: StateFlow<CorrectionSessionReview?> = _state.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun load(sessionId: Int) {
        viewModelScope.launch {
            when (val r = profileRepository.getCorrectionSession(sessionId)) {
                is Result.Success -> {
                    _error.value = null
                    _state.value = r.data
                }
                is Result.Error -> {
                    _error.value = r.message
                    _state.value = null
                }
                else -> {}
            }
        }
    }
}
