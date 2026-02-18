package com.mit.learning_english.domain.usecase

import com.mit.learning_english.domain.repository.AuthRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

/**
 * Use case để kiểm tra trạng thái đăng nhập
 * 
 * Sử dụng trong Splash screen để quyết định navigation
 */
class CheckLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Kiểm tra trạng thái đăng nhập hiện tại
     * 
     * @return Result<Boolean> true nếu đã đăng nhập và token hợp lệ, false nếu chưa
     */
    suspend operator fun invoke(): Result<Boolean> {
        return authRepository.checkLoggedIn()
    }
}
