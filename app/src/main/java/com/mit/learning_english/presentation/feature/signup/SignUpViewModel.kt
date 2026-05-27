package com.mit.learning_english.presentation.feature.signup

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.auth.SignUpUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.shared.UiErrorKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SignUpViewModel – ViewModel quản lý trạng thái và logic màn hình Đăng ký.
 *
 * Kiến trúc MVVM:
 * - State: [SignUpState] (isLoading, serverError)
 * - Event: [SignUpEvent] (NavigateToLogin)
 *
 * Luồng đăng ký:
 * 1. UI truyền email, password và fullName vào [onSignUpClick].
 * 2. ViewModel gọi [SignUpUseCase] → [AuthRepositoryImpl.signUp] → [AuthApiService.createUser].
 * 3. Nếu thành công, emit [SignUpEvent.NavigateToLogin] để chuyển sang màn hình đăng nhập.
 * 4. Nếu thất bại, cập nhật [SignUpState.serverError] để UI hiển thị thông báo lỗi.
 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : BaseViewModel<SignUpState, SignUpEvent>(SignUpState()) {

    /**
     * Cập nhật trạng thái khi người dùng thay đổi email.
     * Reset [SignUpState.serverError] để ẩn thông báo lỗi cũ.
     *
     * @param email Chuỗi email mới người dùng đang nhập
     */
    fun setEmail(email: String) {
        setState { copy(serverError = null) }
    }

    /**
     * Placeholder cập nhật mật khẩu (chưa lưu vào state – UI tự giữ).
     *
     * @param password Chuỗi mật khẩu người dùng đang nhập
     */
    fun setPassword(password: String) {
    }

    /**
     * Placeholder cập nhật họ tên (chưa lưu vào state – UI tự giữ).
     *
     * @param fullName Họ tên người dùng đang nhập
     */
    fun setFullName(fullName: String) {
    }

    /**
     * Xử lý sự kiện nhấn nút "Đăng ký".
     *
     * Validate rằng tất cả 3 trường đều không rỗng, rồi gọi [SignUpUseCase]
     * để tạo tài khoản mới qua API `POST /api/user/v1`.
     * - Thành công → emit [SignUpEvent.NavigateToLogin]
     * - Thất bại → cập nhật [SignUpState.serverError] với message lỗi từ server
     *
     * @param email    Email đăng ký
     * @param password Mật khẩu (sẽ được BCrypt ở Backend trước khi lưu)
     * @param fullName Họ và tên đầy đủ
     */
    fun onSignUpClick(email: String?, password: String?, fullName: String?) {
        viewModelScope.launch(exceptionHandler) {
            if (!email.isNullOrEmpty() && !password.isNullOrEmpty() && !fullName.isNullOrEmpty()) {
                setLoading(true)
                val result = signUpUseCase(email, password, fullName)
                setLoading(false)
                if (result.isSuccess) {
                    if (result.getOrNull() == true) {
                        setState { copy(serverError = null) }
                        emitEvent(SignUpEvent.NavigateToLogin)
                    } else {
                        setState { copy(serverError = UiErrorKey.SIGNUP_FAILED) }
                    }
                } else if (result is Result.Error) {
                    setState { copy(serverError = result.message) }
                }
            }
        }
    }

}