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
import androidx.recyclerview.widget.GridLayoutManager
import com.mit.learning_english.databinding.FragmentOnboardingChooseGenresBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingChooseGenresFragment : Fragment() {

    private var _binding: FragmentOnboardingChooseGenresBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: OnboardingSecondViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    private val adapter = OnboardingGenreGridAdapter { genreId ->
        sharedViewModel.toggleGenre(genreId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingChooseGenresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvGenre.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvGenre.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.uiState.collectLatest { state ->
                    adapter.submitGenres(state.genres, state.selectedGenreIds)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
