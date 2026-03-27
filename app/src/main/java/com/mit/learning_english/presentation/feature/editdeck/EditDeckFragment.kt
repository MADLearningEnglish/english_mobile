package com.mit.learning_english.presentation.feature.editdeck

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.mit.learning_english.databinding.FragmentEditDeckBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditDeckFragment : BaseFragment<FragmentEditDeckBinding, EditDeckViewModel>() {

    override val viewModel: EditDeckViewModel by viewModels()

    // Using FlashcardEditAdapter (or reusing FlashcardInputAdapter)
    // For now I'll create FlashcardEditAdapter later if needed, 
    // but FlashcardInputAdapter can be reused if its parameters match. Since FlashcardInput != FlashcardUpdateInput,
    // I need a separate adapter: FlashcardEditAdapter
    private var pendingImageIndex: Int? = null

    private val selectCoverImageLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.onCoverImageSelected(it) }
    }

    private val selectVisualCueLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uri ->
            pendingImageIndex?.let { index ->
                viewModel.updateVisualCueUri(index, uri)
                pendingImageIndex = null
            }
        }
    }

    private val adapter by lazy {
        FlashcardEditAdapter(
            onToggleExpand = { viewModel.toggleExpanded(it) },
            onDelete = { viewModel.removeFlashcard(it) },
            onWordChanged = { i, v -> viewModel.updateWord(i, v) },
            onPhoneticChanged = { i, v -> viewModel.updatePhonetic(i, v) },
            onMeaningChanged = { i, v -> viewModel.updateMeaning(i, v) },
            onExampleChanged = { i, v -> viewModel.updateExample(i, v) },
            onVisualCueClick = { index ->
                pendingImageIndex = index
                selectVisualCueLauncher.launch("image/*")
            }
        )
    }

    override fun verifyBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEditDeckBinding.inflate(inflater, container, false)

    override fun setupView() {
        binding.rvFlashcards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@EditDeckFragment.adapter
            itemAnimator = null // prevents flicker when toggling expanded/collapsed
        }
        
        binding.tvTitle.text = "Chỉnh sửa bộ thẻ"
    }

    override fun bindView() {
        binding.btnBack.setOnClickListener { viewModel.onNavigateBack() }
        binding.btnCancel.setOnClickListener { viewModel.onNavigateBack() }
        binding.btnSave.setOnClickListener { viewModel.saveDeck() }
        binding.btnAddFlashcard.setOnClickListener { viewModel.addFlashcard() }
        binding.cardCoverImage.setOnClickListener { selectCoverImageLauncher.launch("image/*") }

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
                    // Set title if it's the first load
                    if (binding.etDeckTitle.text.toString().isEmpty() && state.title.isNotEmpty()) {
                        binding.etDeckTitle.setText(state.title)
                    }

                    // Word counter
                    val countText = "${state.wordCount} / ${state.maxWords} từ"
                    binding.tvWordCountLabel.text = countText
                    binding.progressWordCount.progress = state.wordCount

                    // Cover image preview
                    if (state.coverImageUri != null) {
                        binding.imgDeckCover.setImageURI(state.coverImageUri)
                        binding.layoutUploadPrompt.isVisible = false
                    } else if (state.coverImageUrl != null) {
                        com.bumptech.glide.Glide.with(this@EditDeckFragment)
                            .load(state.coverImageUrl)
                            .centerCrop()
                            .into(binding.imgDeckCover)
                        binding.layoutUploadPrompt.isVisible = false
                    } else {
                        binding.imgDeckCover.setImageDrawable(null)
                        binding.layoutUploadPrompt.isVisible = true
                    }

                    // Save button loading state
                    val isLoading = state.isSaving || state.isUploadingImages
                    binding.btnSave.isEnabled = !isLoading
                    binding.btnSave.text = if (isLoading) "Đang lưu..." else "Lưu thay đổi"

                    // Add button: disable at limit
                    binding.btnAddFlashcard.alpha = if (state.isAtLimit) 0.4f else 1.0f
                    binding.btnAddFlashcard.isEnabled = !state.isAtLimit

                    // Active flashcards (status != 0)
                    val activeCards = state.flashcards.mapIndexed { originalIndex, card ->
                        Pair(originalIndex, card)
                    }.filter { it.second.status != 0 }

                    val items = activeCards.map { (originalIndex, card) ->
                        FlashcardEditUiItem(card, originalIndex, state.expandedIndex == originalIndex)
                    }
                    adapter.submitList(items)
                }
            }
        }

        // Observe one-time events
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is EditDeckEvent.NavigateBack -> findNavController().navigateUp()
                        is EditDeckEvent.ShowSuccessDialog -> showSuccessDialog(event.deckId)
                        is EditDeckEvent.ShowSnackbar ->
                            Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun showLoading() { /* managed via state.isLoading */ }
    override fun hideLoading() { /* managed via state.isLoading */ }

    private fun showSuccessDialog(deckId: Int) {
        val dialog = BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
        val sheetBinding = DialogCreateSuccessBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)
        dialog.setCancelable(false)
        
        sheetBinding.tvDialogTitle.text = "Cập nhật thành công!"
        sheetBinding.tvDialogMessage.text = "Bộ thẻ của bạn đã được lưu lại."

        sheetBinding.btnStartStudy.setOnClickListener {
            dialog.dismiss()
            findNavController().navigateUp()
        }
        sheetBinding.btnGoToList.setOnClickListener {
            dialog.dismiss()
            findNavController().navigateUp()
        }

        dialog.show()
    }
}
