package com.mit.learning_english.presentation.feature.profile.completed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.domain.model.profile.LearningActivityItem
import com.mit.learning_english.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileCompletedExercisesViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    /** Mặc định tab Exercises như mockup */
    private val params = MutableStateFlow(Pair("EXERCISE", null as String?))

    val items: Flow<PagingData<LearningActivityItem>> = params
        .flatMapLatest { (filter, q) ->
            profileRepository.completedExercisesPager(filter, q)
        }
        .cachedIn(viewModelScope)

    fun setFilter(filter: String) {
        params.value = Pair(filter, params.value.second)
    }

    fun setQuery(q: String?) {
        params.value = Pair(params.value.first, q)
    }
}
