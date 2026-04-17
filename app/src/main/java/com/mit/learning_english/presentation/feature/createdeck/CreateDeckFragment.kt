package com.mit.learning_english.presentation.feature.createdeck

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.mit.learning_english.R
import com.mit.learning_english.databinding.DialogCreateSuccessBinding
import com.mit.learning_english.databinding.FragmentCreateDeckBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateDeckFragment : BaseFragment<FragmentCreateDeckBinding, CreateDeckViewModel>() {

    override val viewModel: CreateDeckViewModel by viewModels()

    private var pendingImageIndex: Int? = null

    private val selectVisualCueLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uri ->
            pendingImageIndex?.let { index ->
                viewModel.updateImageUri(index, uri)
                pendingImageIndex = null
            }
        }
    }

    private val adapter by lazy {
        FlashcardInputAdapter(
            onTermChanged = { i, v -> viewModel.updateTerm(i, v) },
            onDefinitionChanged = { i, v -> viewModel.updateDefinition(i, v) },
            onImagePickRequested = { i ->
                pendingImageIndex = i
                selectVisualCueLauncher.launch("image/*")
            },
            onDeleteRequested = { i -> viewModel.removeFlashcard(i) }
        )
    }

    override fun verifyBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateDeckBinding.inflate(inflater, container, false)

    override fun setupView() {
        binding.rvFlashcards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CreateDeckFragment.adapter
            itemAnimator = null // prevents flicker when toggling expanded/collapsed
        }

        // Dynamically adjust RecyclerView bottom padding to match keyboard height.
        // This ensures the last items are always scrollable above the keyboard.
        val rvDefaultBottomPadding = binding.rvFlashcards.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.rvFlashcards) { view, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            // When keyboard is visible use its height; otherwise keep default padding
            val extraPadding = if (imeHeight > 0) imeHeight else navBarHeight
            view.updatePadding(bottom = maxOf(rvDefaultBottomPadding, extraPadding + 16))
            insets
        }
    }

    override fun bindView() {
        binding.btnBack.setOnClickListener { viewModel.onNavigateBack() }
        binding.btnSave.setOnClickListener { viewModel.saveDeck() }
        binding.btnAddFlashcard.setOnClickListener { viewModel.addFlashcard() }

        binding.etDeckTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.onTitleChanged(s?.toString() ?: "")
            }
        })


    }

    override fun observeViewModel() {
        super.observeViewModel()

        // Observe UI state
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    // Save button loading state
                    val isLoading = state.isSaving || state.isUploadingImages
                    binding.btnSave.isEnabled = !isLoading
                    binding.btnSave.alpha = if (isLoading) 0.5f else 1.0f

                    // Add button: disable at limit
                    binding.btnAddFlashcard.alpha = if (state.isAtLimit) 0.4f else 1.0f
                    binding.btnAddFlashcard.isEnabled = !state.isAtLimit

                    // Flashcard list → convert to UI items
                    val items = state.flashcards.mapIndexed { i, card ->
                        FlashcardUiItem(card, i)
                    }
                    // Add logic to scroll to bottom when a new card is added
                    val previousSize = adapter.currentList.size
                    adapter.submitList(items) {
                        if (items.size > previousSize && previousSize > 0) {
                            binding.nestedScrollView.postDelayed({
                                binding.nestedScrollView.smoothScrollTo(0, binding.nestedScrollView.getChildAt(0).height)
                            }, 50)
                        }
                    }
                }
            }
        }

        // Observe one-time events
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is CreateDeckEvent.NavigateBack -> findNavController().navigateUp()
                        is CreateDeckEvent.ShowSuccessDialog -> showSuccessDialog(event.deckId)
                        is CreateDeckEvent.ShowSnackbar ->
                            Snackbar.make(
                                binding.root,
                                resolveUiMessage(event.message),
                                Snackbar.LENGTH_SHORT
                            ).show()
                    }
                }
            }
        }
    }

    override fun showLoading() { /* managed via state.isSaving */ }
    override fun hideLoading() { /* managed via state.isSaving */ }

    private fun showSuccessDialog(deckId: Int) {
        val dialog = BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
        val sheetBinding = DialogCreateSuccessBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)
        dialog.setCancelable(false)

        sheetBinding.btnStartStudy.setOnClickListener {
            dialog.dismiss()
            val bundle = Bundle().apply {
                putInt("deckId", deckId)
                putString("deckTitle", getString(R.string.nav_study))
            }
            val navOptions = androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.mainFragment, false)
                .build()
            findNavController().navigate(R.id.studyFragment, bundle, navOptions)
        }
        sheetBinding.btnGoToList.setOnClickListener {
            dialog.dismiss()
            findNavController().navigateUp()
        }

        dialog.show()
    }
}
