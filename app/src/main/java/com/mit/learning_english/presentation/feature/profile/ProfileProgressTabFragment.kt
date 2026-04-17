package com.mit.learning_english.presentation.feature.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentProfileProgressTabBinding
import kotlinx.coroutines.launch

class ProfileProgressTabFragment : Fragment() {

    private var _binding: FragmentProfileProgressTabBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileProgressTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardWordsLearned.setOnClickListener { profileViewModel.openVocabulary() }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: ProfileUiState) {
        state.profile
        val s = state.stats
        val pct = state.knowledgePercent()
        binding.donutKnowledge.progress = pct / 100f
        binding.tvKnowledgePercent.text = getString(R.string.profile_percent_format, pct)

        binding.tvWordsLearned.text = getString(
            R.string.profile_words_learned_format,
            (s?.wordsLearnedCount ?: 0L).toInt()
        )

        // Study days / activity-day entry removed from profile progress tab.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
