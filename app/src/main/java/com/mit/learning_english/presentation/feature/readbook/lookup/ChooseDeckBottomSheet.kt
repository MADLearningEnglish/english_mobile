package com.mit.learning_english.presentation.feature.readbook.lookup

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mit.learning_english.R
import com.mit.learning_english.databinding.BottomsheetChooseDeckBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChooseDeckBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetChooseDeckBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeckPickerViewModel by viewModels()

    private val deckAdapter by lazy {
        DeckPickerAdapter { deck ->
            setFragmentResult(
                REQUEST_KEY_DECK_PICKED,
                bundleOf(
                    BUNDLE_KEY_DECK_ID to deck.id,
                    BUNDLE_KEY_DECK_TITLE to deck.title
                )
            )
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetChooseDeckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvDecks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = deckAdapter
        }
        observeState()
        viewModel.loadDecks()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressDecks.isVisible = state.isLoading
                    binding.rvDecks.isVisible = !state.isLoading && state.decks.isNotEmpty()
                    val message = when {
                        state.errorMessage != null -> state.errorMessage
                        !state.isLoading && state.decks.isEmpty() -> getString(R.string.choose_deck_empty)
                        else -> null
                    }
                    binding.tvDeckMessage.isVisible = message != null
                    binding.tvDeckMessage.text = message.orEmpty()
                    deckAdapter.submitList(state.decks)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvDecks.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "ChooseDeckBottomSheet"
        const val REQUEST_KEY_DECK_PICKED = "request_key_deck_picked"
        const val BUNDLE_KEY_DECK_ID = "bundle_key_deck_id"
        const val BUNDLE_KEY_DECK_TITLE = "bundle_key_deck_title"

        fun show(fragmentManager: FragmentManager) {
            ChooseDeckBottomSheet().show(fragmentManager, TAG)
        }
    }
}
