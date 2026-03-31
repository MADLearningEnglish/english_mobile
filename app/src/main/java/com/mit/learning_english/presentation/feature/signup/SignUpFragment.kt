package com.mit.learning_english.presentation.feature.signup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentSignUpBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

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
        collectState(viewModel.isLoading) { loading ->
            binding.overlayLoading.isVisible = loading
        }
        collectStateProperty(viewModel.uiState, { it.serverError }) { serverError ->
            if (serverError != null) {
                binding.tvServerError.text = serverError
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