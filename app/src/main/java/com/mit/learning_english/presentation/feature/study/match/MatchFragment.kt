package com.mit.learning_english.presentation.feature.study.match

import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mit.learning_english.databinding.FragmentMatchBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MatchFragment : BaseFragment<FragmentMatchBinding, MatchViewModel>() {

    override val viewModel: MatchViewModel by viewModels()

    private lateinit var adapter: MatchCardAdapter
    private var tts: TextToSpeech? = null

    override fun verifyBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMatchBinding = FragmentMatchBinding.inflate(inflater, container, false)

    override fun setupView() {
        adapter = MatchCardAdapter { cardId ->
            viewModel.onCardClicked(cardId)
        }
        
        binding.rvCards.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCards.adapter = adapter

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    override fun bindView() {
        binding.btnStartPlay.setOnClickListener {
            viewModel.startGame()
        }
        binding.btnBack.setOnClickListener {
            viewModel.onNavigateBack()
        }
        binding.btnCompleteBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSpeaker.setOnClickListener {
            speakCurrentTerms()
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    renderState(state)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collectLatest { isLoading ->
                    binding.progressLoading.isVisible = isLoading
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is MatchEvent.NavigateBack -> findNavController().popBackStack()
                        is MatchEvent.SessionComplete -> showCompleteState()
                    }
                }
            }
        }
    }

    private fun renderState(state: MatchState) {
        if (state.isComplete) {
            showCompleteState()
            return
        }

        if (state.isPreGame) {
            binding.layoutPreGame.visibility = View.VISIBLE
            binding.layoutGame.visibility = View.GONE
            binding.layoutComplete.visibility = View.GONE
        } else {
            binding.layoutPreGame.visibility = View.GONE
            binding.layoutGame.visibility = View.VISIBLE
            binding.layoutComplete.visibility = View.GONE

            binding.tvRoundProgress.text = "Lượt: ${state.currentRound}/${state.totalRounds}"
            adapter.submitList(state.cardsOnScreen)
        }
    }

    private fun showCompleteState() {
        binding.layoutPreGame.visibility = View.GONE
        binding.layoutGame.visibility = View.GONE
        binding.layoutComplete.visibility = View.VISIBLE
    }

    private fun speakCurrentTerms() {
        // Can read out loud the selected item or all items on string if needed
    }

    override fun onDestroyView() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroyView()
    }
}
