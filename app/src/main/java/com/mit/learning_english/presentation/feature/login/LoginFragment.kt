package com.mit.learning_english.presentation.feature.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentLoginBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {
    override val viewModel: LoginViewModel by viewModels()
    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun setupView() {

    }

    override fun bindView() {
        binding.btnLogin.setOnClickListener {
            viewModel.onLoginClick()
        }
        binding.btnSingUp.setOnClickListener {
            viewModel.onSignUpClick()
        }
        binding.edtEmail.doOnTextChanged { text, start, before, count ->
            viewModel.setEmail(text.toString())
        }
        binding.edtPassword.doOnTextChanged { text, start, before, count ->
            viewModel.setPassword(text.toString())
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    handleNavigate(event)
                }
            }
        }
    }

    override fun showLoading() {
        binding.overlayLoading.isVisible = true
    }

    override fun hideLoading() {
        binding.overlayLoading.isVisible = false
    }

    private fun handleNavigate(event: LoginEvent) {
        val navController = findNavController()
        when (event) {
            is LoginEvent.NavigateToHome -> {
                navController.navigate(R.id.main_graph)
            }

            is LoginEvent.NavigateToSignUp -> {
                navController.navigate(R.id.signUpFragment)
            }
        }
    }

}