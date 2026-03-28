package com.mit.learning_english.presentation.feature.decklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentDeckListBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.feature.main.MainFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeckListFragment : BaseFragment<FragmentDeckListBinding, DeckListViewModel>() {

    override val viewModel: DeckListViewModel by viewModels()

    private val adapter by lazy {
        DeckListAdapter(
            onStartClick = { deck -> viewModel.onStartStudy(deck.id, deck.title) },
            onEditClick = { deck -> viewModel.onEditDeck(deck.id) },
            onDeleteClick = { deck -> viewModel.onDeleteDeckRequest(deck.id, deck.title) }
        )
    }

    override fun verifyBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDeckListBinding {
        return FragmentDeckListBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        binding.rvDecks.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
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

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
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
                    when (event) {
                        is DeckListEvent.NavigateToStudy -> {
                            val action = MainFragmentDirections.actionMainFragmentToStudyFragment(deckId = event.deckId, deckTitle = event.deckTitle)

                            findNavController().navigate(action)
                        }
                        is DeckListEvent.NavigateToEditDeck -> {
                            val action = MainFragmentDirections.actionMainFragmentToEditDeckFragment(deckId = event.deckId)

                            findNavController().navigate(action)
                        }
                        is DeckListEvent.NavigateToCreateDeck -> {
                            val action = MainFragmentDirections.actionMainFragmentToCreateDeckFragment()

                            findNavController().navigate(action)
                        }
                        is DeckListEvent.ShowDeleteConfirmDialog -> {
                            showDeleteDialog(event.deckId, event.deckTitle)
                        }
                        is DeckListEvent.ShowSnackbar -> {
                            Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
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
            .setTitle("Xóa bộ thẻ")
            .setMessage("Bạn có chắc muốn xóa \"$deckTitle\" không? Hành động này không thể hoàn tác.")
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.onConfirmDelete(deckId)
            }
            .show()
    }
}