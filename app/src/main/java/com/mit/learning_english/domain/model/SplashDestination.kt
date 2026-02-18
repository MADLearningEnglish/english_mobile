package com.mit.learning_english.domain.model

/**
 * Đích đến sau khi xử lý logic Splash
 * 
 * Domain model - không phụ thuộc vào UI/navigation
 */
sealed class SplashDestination {
    object Login : SplashDestination()
    object HomeOnline : SplashDestination()
    object HomeOffline : SplashDestination()
}
