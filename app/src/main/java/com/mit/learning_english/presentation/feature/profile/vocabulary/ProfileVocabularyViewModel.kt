package com.mit.learning_english.presentation.feature.profile.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.domain.model.profile.VocabularyWord
import com.mit.learning_english.domain.repository.ProfileRepository
import com.mit.learning_english.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileVocabularyViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val params = MutableStateFlow(Pair("ALL", null as String?))

    val words: Flow<PagingData<VocabularyWord>> = params
        .flatMapLatest { (filter, q) ->
            profileRepository.vocabularyPager(filter, q?.takeIf { it.isNotBlank() })
        }
        .cachedIn(viewModelScope)

    fun setFilter(filter: String) {
        params.value = Pair(filter, params.value.second)
    }

    fun setQuery(q: String?) {
        params.value = Pair(params.value.first, q)
    }

    fun toggleFavorite(w: VocabularyWord) {
        if (w.id <= 0) return
        viewModelScope.launch {
            when (profileRepository.patchVocabulary(w.id, favorite = !w.favorite, needsAttention = null)) {
                is Result.Success -> {
                    params.value = Pair(params.value.first, params.value.second)
                }
                else -> {}
            }
        }
    }
}
