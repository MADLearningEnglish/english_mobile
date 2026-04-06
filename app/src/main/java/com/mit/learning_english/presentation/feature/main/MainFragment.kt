package com.mit.learning_english.presentation.feature.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentMainBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

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
                .setRestoreState(true)
                .setPopUpTo(navController.graph.startDestinationId,
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
}
