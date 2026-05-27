package com.mit.learning_english.presentation.feature.readbook.lookup

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mit.learning_english.R
import com.mit.learning_english.databinding.BottomsheetAddFlashcardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * AddFlashcardBottomSheet – BottomSheet điền thông tin flashcard.
 *
 * Bước cuối cùng trong luồng thêm flashcard:
 * Hiển thị form với `term` (từ) và `definition` (nghĩa) được điền sẵn.
 * Khi người dùng xác nhận, sẽ gọi [AddFlashcardViewModel.submit] để
 * gửi request lên backend.
 */
@AndroidEntryPoint
class AddFlashcardBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetAddFlashcardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddFlashcardViewModel by viewModels()

    private val deckId: Int by lazy { requireArguments().getInt(ARG_DECK_ID) }
    private val deckTitle: String by lazy { requireArguments().getString(ARG_DECK_TITLE).orEmpty() }
    private val defaultTerm: String by lazy { requireArguments().getString(ARG_DEFAULT_TERM).orEmpty() }
    private val defaultDefinition: String by lazy { requireArguments().getString(ARG_DEFAULT_DEFINITION).orEmpty() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetAddFlashcardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = getString(R.string.add_flashcard_title_format, deckTitle)
        binding.etTerm.setText(defaultTerm)
        binding.etDefinition.setText(defaultDefinition)

        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnAdd.setOnClickListener {
            viewModel.submit(
                deckId = deckId,
                term = binding.etTerm.text?.toString().orEmpty(),
                definition = binding.etDefinition.text?.toString().orEmpty()
            )
        }

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressAdd.isVisible = state.isSubmitting
                    binding.btnAdd.isEnabled = !state.isSubmitting
                    binding.btnCancel.isEnabled = !state.isSubmitting

                    state.errorMessage?.takeIf { it.isNotBlank() }?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        viewModel.consumeError()
                    }
                    if (state.isSuccess) {
                        setFragmentResult(
                            REQUEST_KEY_FLASHCARD_ADDED,
                            bundleOf(BUNDLE_KEY_FLASHCARD_ADDED to true)
                        )
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "AddFlashcardBottomSheet"
        const val REQUEST_KEY_FLASHCARD_ADDED = "request_key_flashcard_added"
        const val BUNDLE_KEY_FLASHCARD_ADDED = "bundle_key_flashcard_added"

        private const val ARG_DECK_ID = "arg_deck_id"
        private const val ARG_DECK_TITLE = "arg_deck_title"
        private const val ARG_DEFAULT_TERM = "arg_default_term"
        private const val ARG_DEFAULT_DEFINITION = "arg_default_definition"

        fun show(
            fragmentManager: FragmentManager,
            deckId: Int,
            deckTitle: String,
            defaultTerm: String,
            defaultDefinition: String
        ) {
            AddFlashcardBottomSheet().apply {
                arguments = bundleOf(
                    ARG_DECK_ID to deckId,
                    ARG_DECK_TITLE to deckTitle,
                    ARG_DEFAULT_TERM to defaultTerm,
                    ARG_DEFAULT_DEFINITION to defaultDefinition
                )
            }.show(fragmentManager, TAG)
        }
    }
}
