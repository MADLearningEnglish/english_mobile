package com.mit.learning_english.domain.usecase.auth

import com.mit.learning_english.domain.repository.AuthRepository
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
    suspend operator fun invoke(): Boolean {
        if (!authRepository.hasToken()) {
            return false
        }
        if (authRepository.isValidLoggedIn()) {
            return true
        }
        return false
    }
}