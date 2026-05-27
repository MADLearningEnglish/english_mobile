package com.mit.learning_english.presentation.feature.onboardingafterlogin

import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.domain.model.LearningLevel

/**
 * Đại diện cho trạng thái giao diện (UI State) của màn hình Onboarding sau khi đăng nhập.
 *
 * @property levels Danh sách các cấp độ học tập hiện có.
 * @property selectedLevelId ID của cấp độ học tập đang được người dùng chọn.
 * @property genres Danh sách các thể loại sách hiện có.
 * @property selectedGenreIds Tập hợp các ID thể loại sách đang được người dùng chọn.
 * @property levelsLoadFailed Cờ đánh dấu việc tải danh sách cấp độ bị thất bại.
 */
data class OnboardingSecondState(
    val levels: List<LearningLevel> = emptyList(),
    val selectedLevelId: Int? = null,
    val genres: List<Genre> = emptyList(),
    val selectedGenreIds: Set<Int> = emptySet(),
    val levelsLoadFailed: Boolean = false
)
