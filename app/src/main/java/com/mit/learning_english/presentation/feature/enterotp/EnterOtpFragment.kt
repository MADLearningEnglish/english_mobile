package com.mit.learning_english.presentation.feature.enterotp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentEnterOtpBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterOtpFragment : BaseFragment<FragmentEnterOtpBinding, EnterOtpViewModel>() {
    override val viewModel: EnterOtpViewModel by viewModels()

    override fun verifyBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEnterOtpBinding.inflate(inflater, container, false)

    override fun setupView() {}

    override fun bindView() {
        // Initialize email from nav args if present
        val emailArg = arguments?.getString("email")
        emailArg?.let { viewModel.setEmail(it) }

        binding.edtOtp.doOnTextChanged { text, _, _, _ ->
            viewModel.setOtp(text.toString())
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.onSubmitOtp()
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        // Collect one-time events from ViewModel (SharedFlow)
        collectEvent(viewModel.event) { event ->
            when (event) {
                is EnterOtpEvent.NavigateToResetPassword -> {
                    val email = viewModel.uiState.value.email ?: ""
                    val otp = viewModel.uiState.value.otp ?: ""
                    val bundle = bundleOf("email" to email, "otp" to otp)
                    findNavController().navigate(R.id.resetPasswordFragment, bundle)
                }
            }
        }

        // Collect UI state using helper from BaseFragment
        collectState(viewModel.uiState) { state ->
            binding.tvOtpError.text = state.serverError ?: ""
        }
    }
}
