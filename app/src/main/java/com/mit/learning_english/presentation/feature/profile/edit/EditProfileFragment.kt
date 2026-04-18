package com.mit.learning_english.presentation.feature.profile.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentProfileEditBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.shared.MediaUrlResolver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentProfileEditBinding, EditProfileViewModel>() {

    override val viewModel: EditProfileViewModel by viewModels()
    private var lastAvatarUrl: String? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.uploadAvatar(it) }
    }

    override fun verifyBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileEditBinding = FragmentProfileEditBinding.inflate(inflater, container, false)

    override fun setupView() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        val openPicker = { pickImage.launch("image/*") }
        binding.imgAvatar.setOnClickListener { openPicker() }
        binding.tvUpdateAvatar.setOnClickListener { openPicker() }
        binding.tvTapPhoto.setOnClickListener { openPicker() }
        binding.btnSave.setOnClickListener {
            viewModel.save(binding.etDisplayName.text?.toString().orEmpty())
        }
        binding.btnDiscard.setOnClickListener { findNavController().navigateUp() }
    }

    override fun bindView() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { s ->
                        if (binding.etDisplayName.text.isNullOrEmpty()) {
                            binding.etDisplayName.setText(s.displayName)
                        }
                        binding.etEmail.setText(s.email)
                        val url = MediaUrlResolver.resolve(s.avatarUrl)
                        if (!url.isNullOrBlank() && url != lastAvatarUrl) {
                            findNavController().previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("profile_avatar_url", url)
                            lastAvatarUrl = url
                        }
                        if (!url.isNullOrBlank()) {
                            Glide.with(this@EditProfileFragment).load(url).circleCrop()
                                .into(binding.imgAvatar)
                        }
                    }
                }
                launch {
                    viewModel.event.collect { ev ->
                        when (ev) {
                            is EditProfileEvent.Saved -> findNavController().navigateUp()
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
