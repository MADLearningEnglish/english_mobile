package com.mit.learning_english.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.mit.learning_english.databinding.ActivitySplashBinding
import com.mit.learning_english.presentation.base.BaseActivity
import com.mit.learning_english.presentation.viewmodels.SplashNavigationEvent
import com.mit.learning_english.presentation.viewmodels.SplashViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {
    override val viewModel: SplashViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(inflater)
    }

    override fun setupView() {
        // Không cần setup gì đặc biệt cho splash screen
    }

    override fun bindView() {
        // Bắt đầu kiểm tra và navigation
        viewModel.checkAndNavigate()
        
        // Observe navigation events
        observeNavigation()
    }

    private fun observeNavigation() {
        viewModel.navigationEvent.observe(this) { event ->
            when (event) {
                is SplashNavigationEvent.NavigateToLogin -> {
                    navigateToLogin()
                }
                is SplashNavigationEvent.NavigateToHomeOnline -> {
                    navigateToHome(isOfflineMode = false)
                }
                is SplashNavigationEvent.NavigateToHomeOffline -> {
                    navigateToHome(isOfflineMode = true)
                }
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, OnboardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToHome(isOfflineMode: Boolean) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("is_offline_mode", isOfflineMode)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}