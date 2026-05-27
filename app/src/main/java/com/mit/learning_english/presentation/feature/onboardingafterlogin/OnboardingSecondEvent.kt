package com.mit.learning_english.presentation.feature.onboardingafterlogin

/**
 * Các sự kiện người dùng tương tác trong màn hình Onboarding thứ hai (sau đăng nhập).
 */
sealed class OnboardingSecondEvent {
    /**
     * Sự kiện chuyển tiếp sang trang (bước) onboarding tiếp theo.
     */
    data object AdvancePage : OnboardingSecondEvent()

    /**
     * Sự kiện hoàn thành quá trình onboarding để chuyển vào màn hình chính.
     */
    data object Complete : OnboardingSecondEvent()
}
