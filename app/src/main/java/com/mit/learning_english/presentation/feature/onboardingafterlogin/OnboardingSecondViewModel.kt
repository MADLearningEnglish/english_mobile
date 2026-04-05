package com.mit.learning_english.presentation.feature.onboardingafterlogin

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.R
import com.mit.learning_english.domain.usecase.genre.GetGenresUseCase
import com.mit.learning_english.domain.usecase.level.GetLevelsUseCase
import com.mit.learning_english.domain.usecase.onboarding.SetAfterLoginOnboardingCompletedUseCase
import com.mit.learning_english.domain.usecase.user.UpdateUserFavoriteGenresUseCase
import com.mit.learning_english.domain.usecase.user.UpdateUserLevelUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingSecondViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val getGenresUseCase: GetGenresUseCase,
    private val getLevelsUseCase: GetLevelsUseCase,
    private val updateUserLevelUseCase: UpdateUserLevelUseCase,
    private val updateUserFavoriteGenresUseCase: UpdateUserFavoriteGenresUseCase,
    private val setAfterLoginOnboardingCompletedUseCase: SetAfterLoginOnboardingCompletedUseCase
) : BaseViewModel<OnboardingSecondState, OnboardingSecondEvent>(OnboardingSecondState()) {

    init {
        loadLevels()
        loadGenres()
    }

    fun selectLevel(levelId: Int) {
        setState { copy(selectedLevelId = levelId) }
    }

    fun toggleGenre(genreId: Int) {
        setState {
            val next = selectedGenreIds.toMutableSet()
            if (!next.add(genreId)) next.remove(genreId)
            copy(selectedGenreIds = next)
        }
    }

    fun submitLevelAndContinue() {
        viewModelScope.launch(exceptionHandler) {
            val levelId = uiState.value.selectedLevelId
            if (levelId == null) {
                emitError(appContext.getString(R.string.onboarding_error_select_level))
                return@launch
            }
            setLoading(true)
            when (val result = updateUserLevelUseCase(levelId)) {
                is Result.Success -> {
                    setLoading(false)
                    emitEvent(OnboardingSecondEvent.AdvancePage)
                }
                is Result.Error -> {
                    setLoading(false)
                    emitError(result.message)
                }
                is Result.Loading -> setLoading(false)
            }
        }
    }

    fun submitGenresAndFinish() {
        viewModelScope.launch(exceptionHandler) {
            val ids = uiState.value.selectedGenreIds
            if (ids.size < 3) {
                emitError(appContext.getString(R.string.onboarding_error_select_three_genres))
                return@launch
            }
            when (val result = updateUserFavoriteGenresUseCase(ids.toList())) {
                is Result.Success -> {
                    setLoading(false)
                    setOnboardingCompleted()
                }
                is Result.Error -> {
                    setLoading(false)
                    emitError(result.message)
                }
                is Result.Loading -> setLoading(false)
            }
        }

    }

    fun setOnboardingCompleted() {
        viewModelScope.launch(exceptionHandler) {
            setAfterLoginOnboardingCompletedUseCase(true)
            emitEvent(OnboardingSecondEvent.Complete)
        }
    }

    fun skipOnboarding() {
        viewModelScope.launch(exceptionHandler) {
            setAfterLoginOnboardingCompletedUseCase(true)
            emitEvent(OnboardingSecondEvent.Complete)
        }
    }

    private fun loadLevels() {
        viewModelScope.launch(exceptionHandler) {
            getLevelsUseCase().onSuccess { levelList ->
                val sorted = levelList.sortedBy { it.id }
                if (sorted.isEmpty()) {
                    setState { copy(levelsLoadFailed = true) }
                    emitError(appContext.getString(R.string.onboarding_error_load_levels))
                } else {
                    setState {
                        copy(
                            levels = sorted,
                            selectedLevelId = sorted.first().id,
                            levelsLoadFailed = false
                        )
                    }
                }
            }.onError {
                setState { copy(levelsLoadFailed = true) }
                emitError(it.message)
            }
        }
    }

    private fun loadGenres() {
        viewModelScope.launch(exceptionHandler) {
            when (val result = getGenresUseCase()) {
                is Result.Success -> setState { copy(genres = result.data) }
                is Result.Error -> emitError(result.message)
                is Result.Loading -> Unit
            }
        }
    }
}
