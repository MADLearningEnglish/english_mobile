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

/**
 * ViewModel xử lý nghiệp vụ cho luồng Onboarding sau khi đăng nhập.
 * Quản lý tải cấp độ học tập, lưu cấp độ học tập của người dùng, tải danh sách thể loại và lưu thể loại yêu thích.
 */
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

    /**
     * Cập nhật cấp độ học tập được lựa chọn vào UI state.
     */
    fun selectLevel(levelId: Int) {
        setState { copy(selectedLevelId = levelId) }
    }

    /**
     * Bật/Tắt lựa chọn một thể loại sách (thêm vào hoặc xóa khỏi danh sách thể loại được chọn).
     */
    fun toggleGenre(genreId: Int) {
        setState {
            val next = selectedGenreIds.toMutableSet()
            if (!next.add(genreId)) next.remove(genreId)
            copy(selectedGenreIds = next)
        }
    }

    /**
     * Gửi yêu cầu cập nhật cấp độ học tập của người dùng lên hệ thống và chuyển trang onboarding kế tiếp nếu thành công.
     */
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

    /**
     * Gửi yêu cầu cập nhật các thể loại sách yêu thích của người dùng (yêu cầu chọn tối thiểu 3 thể loại),
     * sau đó hoàn tất quá trình onboarding nếu thành công.
     */
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

    /**
     * Đánh dấu trạng thái đã hoàn thành onboarding sau đăng nhập vào Local Storage/DataStore và phát sự kiện Complete.
     */
    fun setOnboardingCompleted() {
        viewModelScope.launch(exceptionHandler) {
            setAfterLoginOnboardingCompletedUseCase(true)
            emitEvent(OnboardingSecondEvent.Complete)
        }
    }

    /**
     * Bỏ qua luồng onboarding và đi thẳng vào màn hình chính của ứng dụng.
     */
    fun skipOnboarding() {
        viewModelScope.launch(exceptionHandler) {
            setAfterLoginOnboardingCompletedUseCase(true)
            emitEvent(OnboardingSecondEvent.Complete)
        }
    }

    /**
     * Tải danh sách các cấp độ học tập từ hệ thống.
     */
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

    /**
     * Tải danh sách các thể loại sách từ hệ thống.
     */
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
