package com.mit.learning_english.presentation.feature.vocabulary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mit.learning_english.databinding.FragmentVocabularyBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VocabularyFragment : BaseFragment<FragmentVocabularyBinding, VocabularyViewModel>() {

    override val viewModel: VocabularyViewModel by viewModels()

    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentVocabularyBinding {
        return FragmentVocabularyBinding.inflate(inflater, container, false)
    }

    override fun setupView() {}

    override fun bindView() {}
}
