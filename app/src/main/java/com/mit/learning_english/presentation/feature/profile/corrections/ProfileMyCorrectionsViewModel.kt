package com.mit.learning_english.presentation.feature.profile.corrections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map as pagingMap
import com.mit.learning_english.domain.model.profile.UserCorrectionItem
import com.mit.learning_english.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MyCorrectionsEvent {
    data object Cleared : MyCorrectionsEvent()
    data class ClearFailed(val message: String) : MyCorrectionsEvent()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileMyCorrectionsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val params = MutableStateFlow(Pair("ALL", null as String?))
    private val refreshSignal = MutableStateFlow(0L)

    private val _events = MutableSharedFlow<MyCorrectionsEvent>()
    val events: SharedFlow<MyCorrectionsEvent> = _events

    val rows: Flow<PagingData<CorrectionRow>> = combine(params, refreshSignal) { p, _ -> p }
        .flatMapLatest { (filter, q) ->
            profileRepository.correctionsPager(filter, q).map { pd: PagingData<UserCorrectionItem> ->
                pd.pagingMap { item -> CorrectionRow.EntryRow(item) }
                    .insertSeparators { before: CorrectionRow.EntryRow?, after: CorrectionRow.EntryRow? ->
                        if (after == null) return@insertSeparators null
                        val bk = before?.item?.let { CorrectionTimeFormatter.bucketKey(it.occurredAt) }
                        val ak = CorrectionTimeFormatter.bucketKey(after.item.occurredAt)
                        if (bk != ak) {
                            CorrectionRow.Header(CorrectionTimeFormatter.bucketTitle(after.item.occurredAt))
                        } else {
                            null
                        }
                    }
            }
        }
        .cachedIn(viewModelScope)

    fun setFilter(filter: String) {
        params.value = Pair(filter, params.value.second)
    }

    fun setQuery(q: String?) {
        params.value = Pair(params.value.first, q)
    }

    fun clearHistory() {
        viewModelScope.launch {
            when (val r = profileRepository.clearCorrections()) {
                is com.mit.learning_english.domain.util.Result.Success -> {
                    refresh()
                    _events.emit(MyCorrectionsEvent.Cleared)
                }
                is com.mit.learning_english.domain.util.Result.Error ->
                    _events.emit(MyCorrectionsEvent.ClearFailed(r.message))
                else -> {}
            }
        }
    }

    fun refresh() {
        refreshSignal.value = refreshSignal.value + 1L
    }
}
