package com.mit.learning_english.presentation.feature.signup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mit.learning_english.databinding.FragmentSignUpBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.mit.learning_english.R

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>() {
    override val viewModel: SignUpViewModel by viewModels()

    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSignUpBinding {
        return FragmentSignUpBinding.inflate(inflater, container, false)
    }

    override fun setupView() {

    }

    override fun bindView() {
        binding.edtEmail.doOnTextChanged { text, start, before, count ->
            viewModel.setEmail(text.toString())
        }
        binding.edtPassword.doOnTextChanged { text, start, before, count ->
            viewModel.setPassword(text.toString())
        }
        binding.edtFullName.doOnTextChanged { text, start, before, count ->
            viewModel.setFullName(text.toString())
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.edtEmail.text?.toString()
            val password = binding.edtPassword.text?.toString()
            val fullName = binding.edtFullName.text?.toString()
            viewModel.onSignUpClick(email, password, fullName)
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectState(viewModel.uiState) { state ->
            binding.overlayLoading.isVisible = state.isLoading
            if (state.errorMessage != null) {
                binding.tvServerError.text = state.errorMessage
                binding.tvServerError.isVisible = true
            } else {
                binding.tvServerError.isVisible = false
            }
        }

        collectEvent(viewModel.event) { event ->
            when (event) {
                is SignUpEvent.NavigateToLogin -> {
                    findNavController().navigate(R.id.loginFragment)
                }
            }
        }
    }
}