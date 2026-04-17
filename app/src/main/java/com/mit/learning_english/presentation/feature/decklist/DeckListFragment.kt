package com.mit.learning_english.presentation.feature.decklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentDeckListBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeckListFragment : BaseFragment<FragmentDeckListBinding, DeckListViewModel>() {

    override val viewModel: DeckListViewModel by viewModels()

    private val adapter by lazy {
        DeckListAdapter(
            onStartClick = { deck -> viewModel.onDeckClick(deck.id, deck.title) },
            onEditClick = { deck -> viewModel.onEditDeck(deck.id) },
            onDeleteClick = { deck -> viewModel.onDeleteDeckRequest(deck.id, deck.title) }
        )
    }

    override fun verifyBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDeckListBinding {
        return FragmentDeckListBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        binding.rvDecks.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = this@DeckListFragment.adapter
            setHasFixedSize(true)
        }
    }

    override fun bindView() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadDecks()
        }

        binding.fabCreate.setOnClickListener {
            viewModel.onCreateDeck()
        }

        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.onSearchTextChanged(s?.toString() ?: "")
            }
        })
    }

    override fun observeViewModel() {
        super.observeViewModel()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(viewModel.uiState, viewModel.isLoading) { state, isLoading ->
                    Pair(state, isLoading)
                }.collectLatest { (state, isLoading) ->
                    binding.swipeRefresh.isRefreshing = false
                    if (isLoading && adapter.itemCount == 0) {
                        showShimmer()
                    } else {
                        hideShimmer()
                    }

                    adapter.submitList(state.decks)

                    if (!isLoading) {
                        if (state.decks.isEmpty()) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                            binding.rvDecks.visibility = View.INVISIBLE
                        } else {
                            binding.layoutEmpty.visibility = View.GONE
                            binding.rvDecks.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        // Observe one-time events
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    val parentNavController =
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    when (event) {
                        is DeckListEvent.NavigateToStudy -> {
                            parentNavController.navigate(
                                R.id.action_mainFragment_to_studyFragment,
                                bundleOf(
                                    "deckId" to event.deckId,
                                    "deckTitle" to event.deckTitle
                                )
                            )
                        }
                        is DeckListEvent.NavigateToQuiz -> {
                            parentNavController.navigate(
                                R.id.action_mainFragment_to_quizFragment,
                                bundleOf(
                                    "deckId" to event.deckId,
                                    "deckTitle" to event.deckTitle
                                )
                            )
                        }
                        is DeckListEvent.NavigateToMatch -> {
                            parentNavController.navigate(
                                R.id.action_mainFragment_to_matchFragment,
                                bundleOf(
                                    "deckId" to event.deckId,
                                    "deckTitle" to event.deckTitle
                                )
                            )
                        }
                        is DeckListEvent.ShowStudyModeDialog -> {
                            showStudyModeDialog(event.deckId, event.deckTitle)
                        }
                        is DeckListEvent.NavigateToEditDeck -> {
                            parentNavController.navigate(
                                R.id.action_mainFragment_to_editDeckFragment,
                                bundleOf("deckId" to event.deckId)
                            )
                        }
                        is DeckListEvent.NavigateToCreateDeck -> {
                            parentNavController.navigate(R.id.action_mainFragment_to_createDeckFragment)
                        }
                        is DeckListEvent.ShowDeleteConfirmDialog -> {
                            showDeleteDialog(event.deckId, event.deckTitle)
                        }
                        is DeckListEvent.ShowSnackbar -> {
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
    }

    // --- ĐÃ THÊM HÀM ONRESUME Ở ĐÂY ---
    override fun onResume() {
        super.onResume()
        // Gọi lại API để làm mới danh sách mỗi khi màn hình này hiển thị lại
        // (Giúp load lại URL ảnh mới nhất nếu vừa được cập nhật)
        viewModel.loadDecks()
    }
    // ----------------------------------

    override fun showLoading() {
        // Loading is handled via uiState in observeViewModel
    }

    override fun hideLoading() {
        // Loading is handled via uiState in observeViewModel
    }

    private fun showShimmer() {
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.shimmerLayout.startShimmer()
        binding.rvDecks.visibility = View.INVISIBLE
        binding.layoutEmpty.visibility = View.GONE
    }

    private fun hideShimmer() {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
    }

    private fun showDeleteDialog(deckId: Int, deckTitle: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.deck_delete_title)
            .setMessage(getString(R.string.deck_delete_message, deckTitle))
            .setNegativeButton(R.string.common_cancel, null)
            .setPositiveButton(R.string.common_delete) { _, _ ->
                viewModel.onConfirmDelete(deckId)
            }
            .show()
    }

    private fun showStudyModeDialog(deckId: Int, deckTitle: String) {
        val options = arrayOf(
            getString(R.string.deck_study_mode_flashcard),
            getString(R.string.deck_study_mode_quiz),
            getString(R.string.deck_study_mode_match),
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.deck_study_mode_title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewModel.onStudyModeSelected(deckId, deckTitle, false) // Flashcard
                    1 -> viewModel.onStudyModeSelected(deckId, deckTitle, true) // Quiz
                    2 -> viewModel.onMatchModeSelected(deckId, deckTitle) // Match
                }
            }
            .show()
    }
}