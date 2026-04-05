package com.mit.learning_english.presentation.feature.onboardingafterlogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.databinding.FragmentOnboardingChooseLevelBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingChooseLevelFragment : Fragment() {

    private var _binding: FragmentOnboardingChooseLevelBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: OnboardingSecondViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    private val adapter = OnboardingLevelAdapter { levelId ->
        sharedViewModel.selectLevel(levelId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingChooseLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvLevels.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLevels.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.uiState.collectLatest { state ->
                    binding.rvLevels.isEnabled = state.levels.isNotEmpty()
                    adapter.submitLevels(state.levels, state.selectedLevelId)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
