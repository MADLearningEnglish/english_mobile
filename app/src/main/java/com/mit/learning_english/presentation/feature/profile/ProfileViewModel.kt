package com.mit.learning_english.presentation.feature.profile

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.repository.ProfileRepository
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ProfileUiState(
    val profile: com.mit.learning_english.domain.model.profile.ProfileMe? = null,
    val stats: com.mit.learning_english.domain.model.profile.LearningStatsOverview? = null,
    val heatmap: List<com.mit.learning_english.domain.model.profile.HeatmapDay> = emptyList(),
    val todayActivities: com.mit.learning_english.domain.model.profile.ActivityDayDetail? = null,
    val errorMessage: String? = null
) {
    /**
     * % hiển thị trên donut: căn theo từ đã học (6 từ ≈ 100% như mockup 3 từ = 50%).
     * Có thể thay bằng field API sau này.
     */
    fun knowledgePercent(): Int {
        val w = stats?.wordsLearnedCount ?: 0L
        return ((w * 100L) / 6L).toInt().coerceIn(0, 100)
    }
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : BaseViewModel<ProfileUiState, ProfileEvent>(ProfileUiState()) {

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            val me = profileRepository.getMe()
            val st = profileRepository.getStatsOverview()
            val hm = profileRepository.getHeatmap(null, null)
            val today = profileRepository.getActivityDay(LocalDate.now())

            val profileErr = (me as? Result.Error)?.message
            val statsErr = (st as? Result.Error)?.message
            if (me is Result.Error) {
                emitError(profileErr ?: "Không tải được hồ sơ")
            } else if (st is Result.Error) {
                emitError(statsErr ?: "Không tải được thống kê")
            }

            setState {
                copy(
                    profile = (me as? Result.Success)?.data ?: profile,
                    stats = (st as? Result.Success)?.data ?: stats,
                    heatmap = (hm as? Result.Success)?.data ?: heatmap,
                    todayActivities = (today as? Result.Success)?.data ?: todayActivities,
                    errorMessage = profileErr ?: statsErr
                )
            }
            setLoading(false)
        }
    }

    fun openEditProfile() = emitEvent(ProfileEvent.OpenEditProfile)
    fun openVocabulary() = emitEvent(ProfileEvent.OpenVocabularyList)
    fun openDailyActivity() = emitEvent(ProfileEvent.OpenDailyActivity)
    fun openActivityDay(date: LocalDate) = emitEvent(ProfileEvent.OpenActivityDay(date))
}
