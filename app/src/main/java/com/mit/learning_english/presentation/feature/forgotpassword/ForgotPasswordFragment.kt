package com.mit.learning_english.presentation.feature.forgotpassword

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentForgotPasswordBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding, ForgotPasswordViewModel>() {
    override val viewModel: ForgotPasswordViewModel by viewModels()

    override fun verifyBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentForgotPasswordBinding.inflate(inflater, container, false)

    override fun setupView() {}

    override fun bindView() {
        binding.edtEmail.doOnTextChanged { text, _, _, _ ->
            viewModel.setEmail(text.toString())
            binding.tvEmailError.text = ""
        }

        binding.btnContinue.setOnClickListener {
            viewModel.onRequestOtp()
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectEvent(viewModel.event) { event ->
            when (event) {
                is ForgotPasswordEvent.NavigateToEnterOtp -> {
                    val email = viewModel.uiState.value.email
                    val bundle = androidx.core.os.bundleOf("email" to email)
                    findNavController().navigate(R.id.enterOtpFragment, bundle)
                }
            }
        }

        collectState(viewModel.uiState) { state ->
            // Display server error under email field
            binding.tvEmailError.text = state.serverError ?: ""
        }
    }
}
