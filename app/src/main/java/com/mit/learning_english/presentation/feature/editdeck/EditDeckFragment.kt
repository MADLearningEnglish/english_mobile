package com.mit.learning_english.presentation.feature.editdeck

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.mit.learning_english.databinding.FragmentEditDeckBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditDeckFragment : BaseFragment<FragmentEditDeckBinding, EditDeckViewModel>() {

    override val viewModel: EditDeckViewModel by viewModels()

    private var isInitialDataLoaded = false

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
        FlashcardEditAdapter(
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
        FragmentEditDeckBinding.inflate(inflater, container, false)

    override fun setupView() {
        binding.rvFlashcards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@EditDeckFragment.adapter
            itemAnimator = null 
        }
        
        binding.tvTitle.text = "Chỉnh sửa bộ thẻ"

        val rvDefaultBottomPadding = binding.rvFlashcards.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.rvFlashcards) { view, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val extraPadding = if (imeHeight > 0) imeHeight else navBarHeight
            view.updatePadding(bottom = maxOf(rvDefaultBottomPadding, extraPadding + 16))
            insets
        }
    }

    override fun bindView() {
        binding.btnBack.setOnClickListener { viewModel.onNavigateBack() }
        binding.btnSave.setOnClickListener { viewModel.saveDeck() }
        binding.btnAddFlashcard.setOnClickListener {
            binding.rvFlashcards.scrollToPosition(adapter.itemCount - 1)
            viewModel.addFlashcard()

        }

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (!isInitialDataLoaded && state.title.isNotEmpty()) {
                        isInitialDataLoaded = true
                        binding.etDeckTitle.setText(state.title)
                    }

                    val isLoading = state.isSaving || state.isUploadingImages
                    binding.btnSave.isEnabled = !isLoading
                    binding.btnSave.alpha = if (isLoading) 0.5f else 1.0f

                    binding.btnAddFlashcard.alpha = if (state.isAtLimit) 0.4f else 1.0f
                    binding.btnAddFlashcard.isEnabled = !state.isAtLimit

                    val activeCards = state.flashcards.mapIndexed { originalIndex, card ->
                        Pair(originalIndex, card)
                    }.filter { it.second.status != 0 }

                    val items = activeCards.map { (originalIndex, card) ->
                        FlashcardEditUiItem(card, originalIndex)
                    }
                    adapter.submitList(items)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is EditDeckEvent.NavigateBack -> findNavController().navigateUp()
                        is EditDeckEvent.ShowSuccessDialog -> showSuccessDialog(event.deckId)
                        is EditDeckEvent.ShowSnackbar -> Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun showLoading() { }
    override fun hideLoading() { }

    private fun showSuccessDialog(deckId: Int) {
        val dialog = BottomSheetDialog(requireContext(), R.style.ThemeOverlay_App_BottomSheetDialog)
        val sheetBinding = DialogCreateSuccessBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)
        dialog.setCancelable(false)
        
        sheetBinding.tvDialogTitle.text = "Cập nhật thành công!"
        sheetBinding.tvDialogMessage.text = "Bộ thẻ của bạn đã được lưu lại."

        sheetBinding.btnStartStudy.setOnClickListener {
            dialog.dismiss()
            val bundle = Bundle().apply {
                putInt("deckId", deckId)
                putString("deckTitle", "Luyện tập")
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
