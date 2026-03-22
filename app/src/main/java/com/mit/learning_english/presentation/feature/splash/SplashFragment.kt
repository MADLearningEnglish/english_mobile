package com.mit.learning_english.presentation.feature.splash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentSplashBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        viewModel.checkAndNavigate()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectEvent(viewModel.event) { event ->
            handleNavigationEvent(event)
        }
    }

    override fun showLoading() {
        binding.loadingLottie.
            playAnimation()

    }

    override fun hideLoading() {
binding.loadingLottie.cancelAnimation()
    }

    private fun handleNavigationEvent(event: SplashEvent) {
        val navController = findNavController()
        val navOptions = androidx.navigation.NavOptions.Builder()
            .setPopUpTo(R.id.splashFragment, true)
            .build()
        when (event) {
            SplashEvent.NavigateToLogin -> {
                navController.navigate(R.id.loginFragment, null, navOptions)
            }
            SplashEvent.NavigateToHome -> {
                navController.navigate(R.id.main_graph, null, navOptions)
            }
        }
    }
}