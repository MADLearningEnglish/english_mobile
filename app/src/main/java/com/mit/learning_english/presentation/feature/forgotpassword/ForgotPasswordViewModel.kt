package com.mit.learning_english.presentation.feature.forgotpassword

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.auth.RequestForgotPasswordOtpUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.shared.UiErrorKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ForgotPasswordViewModel – ViewModel màn hình nhập email quên mật khẩu.
 *
 * Luồng:
 * 1. UI gọi [setEmail] mỗi khi người dùng nhập email.
 * 2. Nhấn "Gửi OTP" → UI gọi [onRequestOtp].
 * 3. Validate email rỗng → hiển thị [ForgotPasswordState.serverError].
 * 4. Gọi [RequestForgotPasswordOtpUseCase] → Backend tạo OTP và gửi email.
 * 5. Thành công → emit [ForgotPasswordEvent.NavigateToEnterOtp].
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val requestOtpUseCase: RequestForgotPasswordOtpUseCase
) : BaseViewModel<ForgotPasswordState, ForgotPasswordEvent>(ForgotPasswordState()) {

    /**
     * Cập nhật email vào state và reset thông báo lỗi server cũ.
     *
     * @param email Chuỗi email người dùng đang nhập
     */
    fun setEmail(email: String) {
        setState { copy(email = email, serverError = null) }
    }

    /**
     * Xử lý sự kiện nhấn "Gửi OTP".
     *
     * Validate email không rỗng, gọi [RequestForgotPasswordOtpUseCase]
     * → API `POST /api/auth/v1/forgot-password/request-otp`.
     * - Thành công → emit [ForgotPasswordEvent.NavigateToEnterOtp]
     * - Email rỗng → set serverError = EMAIL_REQUIRED
     * - Lỗi API → set serverError = message từ server
     */
    fun onRequestOtp() {
        viewModelScope.launch(exceptionHandler) {
            val email = uiState.value.email
            if (email.isNullOrEmpty()) {
                setState { copy(serverError = UiErrorKey.EMAIL_REQUIRED) }
                return@launch
            }

            setLoading(true)
            val result = requestOtpUseCase(email)
            setLoading(false)

            when (result) {
                is Result.Success -> emitEvent(ForgotPasswordEvent.NavigateToEnterOtp)
                is Result.Error -> setState { copy(serverError = result.message) }
                else -> setState { copy(serverError = UiErrorKey.UNKNOWN) }
            }
        }
    }
}
