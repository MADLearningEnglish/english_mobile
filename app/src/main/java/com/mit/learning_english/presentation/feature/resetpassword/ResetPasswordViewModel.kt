package com.mit.learning_english.presentation.feature.resetpassword

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.auth.ResetForgotPasswordUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.shared.UiErrorKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ResetPasswordViewModel – ViewModel màn hình đặt lại mật khẩu.
 *
 * Màn hình này xuất hiện sau khi người dùng đã xác minh OTP thành công.
 * Nhận email và OTP được truyền từ màn hình trước qua Safe Args / Bundle.
 *
 * Luồng:
 * 1. UI gọi [setEmail], [setOtp], [setPassword], [setRePassword] khi nhập liệu.
 * 2. Nhấn "Cập nhật mật khẩu" → UI gọi [onUpdatePassword].
 * 3. Validate: password và rePassword phải giống nhau và không rỗng.
 * 4. Gọi [ResetForgotPasswordUseCase] → API `POST /api/auth/v1/forgot-password/reset`.
 * 5. Thành công → emit [ResetPasswordEvent.NavigateToLogin].
 */
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetUseCase: ResetForgotPasswordUseCase
) : BaseViewModel<ResetPasswordState, ResetPasswordEvent>(ResetPasswordState()) {

    /**
     * Cập nhật mật khẩu mới vào state và reset lỗi cũ.
     * @param password Mật khẩu mới người dùng nhập
     */
    fun setPassword(password: String) {
        setState { copy(password = password, serverError = null) }
    }

    /**
     * Cập nhật mật khẩu xác nhận vào state và reset lỗi cũ.
     * @param rePassword Mật khẩu nhập lại để xác nhận
     */
    fun setRePassword(rePassword: String) {
        setState { copy(rePassword = rePassword, serverError = null) }
    }

    /**
     * Cập nhật email (truyền từ màn hình ForgotPassword) vào state.
     * @param email Email tài khoản cần đặt lại mật khẩu
     */
    fun setEmail(email: String) {
        setState { copy(email = email, serverError = null) }
    }

    /**
     * Cập nhật OTP (truyền từ màn hình VerifyOtp) vào state.
     * @param otp Mã OTP 6 chữ số đã xác minh
     */
    fun setOtp(otp: String) {
        setState { copy(otp = otp, serverError = null) }
    }

    /**
     * Xử lý sự kiện nhấn "Cập nhật mật khẩu".
     *
     * Validate:
     * - password và rePassword không được rỗng
     * - password phải bằng rePassword
     *
     * Nếu hợp lệ, gọi [ResetForgotPasswordUseCase] → API Backend mã hoá
     * mật khẩu mới bằng BCrypt và đánh dấu OTP đã dùng.
     * - Thành công → emit [ResetPasswordEvent.NavigateToLogin]
     * - Validation fail → set serverError = PASSWORD_MISMATCH_OR_EMPTY
     * - Lỗi API → set serverError = message từ server
     */
    fun onUpdatePassword() {
        viewModelScope.launch(exceptionHandler) {
            val pwd = uiState.value.password
            val rePwd = uiState.value.rePassword
            val email = uiState.value.email ?: ""
            val otp = uiState.value.otp ?: ""

            if (pwd.isNullOrEmpty() || rePwd.isNullOrEmpty() || pwd != rePwd) {
                setState { copy(serverError = UiErrorKey.PASSWORD_MISMATCH_OR_EMPTY) }
                return@launch
            }

            setLoading(true)
            val result = resetUseCase(email, otp, pwd)
            setLoading(false)

            when (result) {
                is Result.Success -> emitEvent(ResetPasswordEvent.NavigateToLogin)
                is Result.Error -> setState { copy(serverError = result.message) }
                else -> setState { copy(serverError = UiErrorKey.UNKNOWN) }
            }
        }
    }
}
