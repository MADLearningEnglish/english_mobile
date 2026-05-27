package com.mit.learning_english.presentation.feature.onboardingafterlogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mit.learning_english.R

/**
 * Fragment cho phép người dùng chọn mục tiêu học tập/đọc sách hàng ngày trong quá trình onboarding.
 */
class OnboardingChooseGoalFragment : Fragment() {
    /**
     * Khởi tạo giao diện Fragment từ file layout XML fragment_onboarding_choose_goal.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_choose_goal, container, false)
    }
}
