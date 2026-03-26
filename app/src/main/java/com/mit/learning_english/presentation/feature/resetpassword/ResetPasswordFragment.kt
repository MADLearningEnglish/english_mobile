package com.mit.learning_english.presentation.feature.resetpassword

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentResetPasswordBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding, ResetPasswordViewModel>() {
    override val viewModel: ResetPasswordViewModel by viewModels()

    override fun verifyBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentResetPasswordBinding.inflate(inflater, container, false)

    override fun setupView() {}

    override fun bindView() {
        // Read email and otp from nav args if provided; we don't show inputs for them here
        val emailArg = arguments?.getString("email")
        val otpArg = arguments?.getString("otp")
        emailArg?.let { viewModel.setEmail(it) }
        otpArg?.let { viewModel.setOtp(it) }

        binding.edtPassword.doOnTextChanged { text, _, _, _ -> viewModel.setPassword(text.toString()) }
        binding.edtRePassword.doOnTextChanged { text, _, _, _ -> viewModel.setRePassword(text.toString()) }

        binding.btnUpdate.setOnClickListener { viewModel.onUpdatePassword() }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectEvent(viewModel.event) { event ->
            when (event) {
                is ResetPasswordEvent.NavigateToLogin -> findNavController().navigate(R.id.loginFragment)
            }
        }

        collectState(viewModel.uiState) { state ->
            binding.tvPasswordError.text = state.serverError ?: ""
        }
    }
}
