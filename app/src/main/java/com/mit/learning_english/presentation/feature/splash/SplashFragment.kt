package com.mit.learning_english.presentation.feature.splash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentSplashBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {
    override val viewModel: SplashViewModel by viewModels()

    override fun verifyBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        // Setup UI nếu cần
    }

    override fun bindView() {
        // checkAndNavigate() được gọi sau khi observer đã ready trong observeViewModel()
        viewModel.checkAndNavigate()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectEvent(viewModel.event) { event ->
            handleNavigationEvent(event)
        }
    }

    override fun showLoading() {
        binding.loadingLottie.playAnimation()
    }

    override fun hideLoading() {
        binding.loadingLottie.cancelAnimation()
    }

    private fun handleNavigationEvent(event: SplashEvent) {
        when (event) {
            SplashEvent.NavigateToLogin -> {
               val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                findNavController().navigate(action)
            }
            SplashEvent.NavigateToOnboardingBeforeLogin ->{
                val action = SplashFragmentDirections.actionSplashFragmentToOnboardingFragment()
                findNavController().navigate(action)
            }
            SplashEvent.NavigateToHome -> {
                val action = SplashFragmentDirections.actionSplashFragmentToMain()
                findNavController().navigate(action)
            }
            SplashEvent.NavigateToOnboardingAfterLogin -> {
                val action = SplashFragmentDirections.actionSplashFragmentToOnboardingSecondFragment()
                findNavController().navigate(action)
            }
        }
    }
}