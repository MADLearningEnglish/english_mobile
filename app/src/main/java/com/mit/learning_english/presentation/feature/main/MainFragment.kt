package com.mit.learning_english.presentation.feature.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentMainBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    override val viewModel: MainViewModel by viewModels()

    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNavView.setOnItemSelectedListener { item ->
            val options = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setRestoreState(false)
                .setPopUpTo(
                    navController.graph.startDestinationId,
                    inclusive = false,
                    saveState = true
                )
                .build()

            try {
                navController.navigate(item.itemId, null, options)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    override fun bindView() {}

    override fun observeViewModel() {
        super.observeViewModel()
        observePendingDeepLink()
    }

    /**
     * Observe pending deep link. Chỉ điều hướng khi:
     *  - pending tồn tại (bookId > 0)
     *  - user đã đăng nhập hợp lệ (gate trong ViewModel qua CheckLoggedInUseCase)
     *
     * Nếu chưa đăng nhập, pending KHÔNG bị consume, được giữ lại trong DataStore
     * và sẽ tự động trigger lại khi MainFragment được show sau khi user login xong.
     */
    private fun observePendingDeepLink() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pendingDeepLinkBookId
                    .collect { pending ->
                        if (pending == null || pending <= 0) return@collect
                        val consumed = viewModel.tryConsumeDeepLink() ?: return@collect
                        val action = MainFragmentDirections
                            .actionMainFragmentToBookDetailFragment(consumed)
                        findNavController().navigate(action)
                    }
            }
        }
    }
}
