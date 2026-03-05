package com.mit.learning_english.presentation.feature.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mit.learning_english.databinding.FragmentProfileBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()

    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun setupView() {}

    override fun bindView() {}
}
