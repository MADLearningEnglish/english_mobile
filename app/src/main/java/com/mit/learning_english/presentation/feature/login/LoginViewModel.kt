package com.mit.learning_english.presentation.feature.login

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.auth.LoginUseCase
import com.mit.learning_english.domain.usecase.onboarding.CheckSeenOnboardingAfterLoginUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * LoginViewModel – ViewModel quản lý trạng thái và logic màn hình Đăng nhập.
 *
 * Kiến trúc MVVM:
 * - State: [LoginState] (isLoading, isSuccess, email, password)
 * - Event: [LoginEvent] (NavigateToHome, NavigateToSignUp, NavigateToOnboarding)
 *
 * Luồng đăng nhập:
 * 1. UI cập nhật email/password qua [setEmail] / [setPassword].
 * 2. Người dùng nhấn "Đăng nhập" → UI gọi [onLoginClick].
 * 3. ViewModel gọi [LoginUseCase] (lưu token vào AuthManager/DataStore).
 * 4. Sau khi thành công, gọi [handleCheckOnboarding] để kiểm tra
 *    người dùng đã hoàn thành Onboarding chưa rồi điều hướng phù hợp.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val checkSeenOnboardingAfterLoginUseCase: CheckSeenOnboardingAfterLoginUseCase
) : BaseViewModel<LoginState, LoginEvent>(LoginState()) {

    /**
     * Xử lý sự kiện nhấn nút "Đăng nhập".
     *
     * Validate rằng email và password không rỗng, sau đó gọi [LoginUseCase]
     * với coroutine chạy trên Dispatchers.IO (qua AuthRepositoryImpl).
     * Cập nhật [LoginState.isLoading] trong suốt quá trình gọi API.
     * Nếu thành công, gọi [handleCheckOnboarding] để điều hướng tiếp theo.
     */
    fun onLoginClick() {
        viewModelScope.launch(exceptionHandler) {
            if (uiState.value.email != null && uiState.value.password != null) {
                setLoading(true)
                val result = loginUseCase(uiState.value.email!!, uiState.value.password!!)
                if (result.isSuccess) {
                    setLoading(false)
                    if (result.getOrNull() == true) {
                        setState { copy(isSuccess = true) }
                        handleCheckOnboarding()
                    } else {
                        setState { copy(isSuccess = false) }
                    }
                } else if (result is Result.Error) {
                    setLoading(false)
                    emitError(result.message)
                }
            }
        }
    }

    /**
     * Kiểm tra trạng thái Onboarding sau khi đăng nhập thành công.
     *
     * Gọi [CheckSeenOnboardingAfterLoginUseCase] để xác định người dùng đã
     * xem hướng dẫn sau đăng nhập chưa:
     * - Đã xem → emit [LoginEvent.NavigateToHome]
     * - Chưa xem → emit [LoginEvent.NavigateToOnboarding]
     */
    fun handleCheckOnboarding(){
        viewModelScope.launch{
            checkSeenOnboardingAfterLoginUseCase().onSuccess { isCompleted ->
                if(isCompleted){
                    emitEvent(LoginEvent.NavigateToHome)
                }else{
                    emitEvent(LoginEvent.NavigateToOnboarding)
                }
            }.onError {
                emitError(it.message)
            }
        }
    }

    /**
     * Cập nhật email vào [LoginState] khi người dùng nhập liệu.
     *
     * @param email Chuỗi email người dùng đang nhập
     */
    fun setEmail(email: String) {
        setState { copy(email = email) }
    }

    /**
     * Cập nhật mật khẩu vào [LoginState] khi người dùng nhập liệu.
     *
     * @param password Chuỗi mật khẩu người dùng đang nhập
     */
    fun setPassword(password: String) {
        setState { copy(password = password) }
    }

    /**
     * Xử lý sự kiện nhấn nút "Đăng ký".
     * Emit [LoginEvent.NavigateToSignUp] để Fragment điều hướng sang màn hình đăng ký.
     */
    fun onSignUpClick() {
        emitEvent(LoginEvent.NavigateToSignUp)
    }
}
