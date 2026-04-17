package com.mit.learning_english.presentation.feature.historyreadbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentHistoryReadBookBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.utils.VerticalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryReadBookFragment :
    BaseFragment<FragmentHistoryReadBookBinding, HistoryReadBookViewModel>() {
    private var loadingStartTime = 0L
    private val MIN_LOADING_TIME = 500L


    override val viewModel: HistoryReadBookViewModel by viewModels()
    private lateinit var historyAdapter: HistoryReadBookPagingAdapter

    override fun verifyBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHistoryReadBookBinding {
        return FragmentHistoryReadBookBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        historyAdapter = HistoryReadBookPagingAdapter { book ->
            viewModel.onBookClick(book.id)
        }
        binding.rvHistoryBooks.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalSpacingItemDecoration(12))
        }
    }

    override fun bindView() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.historyBooks.collectLatest { pagingData ->
                    historyAdapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                historyAdapter.loadStateFlow.collectLatest { loadState ->
                    when (val refreshState = loadState.source.refresh) {
                        is LoadState.Loading -> viewModel.loading(true)
                        is LoadState.NotLoading -> {
                            viewModel.loading(false)
                            val isEmpty =
                                loadState.source.append.endOfPaginationReached && historyAdapter.itemCount == 0
                            binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
                        }

                        is LoadState.Error -> {
                            viewModel.loading(false)
                            binding.tvEmptyState.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                refreshState.error.localizedMessage
                                    ?: getString(R.string.failed_to_load_books),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        collectEvent(viewModel.event) { event ->
            when (event) {
                is HistoryReadBookEvent.NavigateToBookDetail -> {
                    findNavController().navigate(
                        R.id.bookDetailFragment,
                        bundleOf("bookId" to event.bookId)
                    )
                }
            }
        }
    }

    override fun showLoading() {
        loadingStartTime = System.currentTimeMillis()
        binding.overlayLoading.visibility = View.VISIBLE
        binding.lottieLoading.playAnimation()
    }

    override fun hideLoading() {
         val elapsed = System.currentTimeMillis() - loadingStartTime
        val remaining = (MIN_LOADING_TIME - elapsed).coerceAtLeast(0)
        lifecycleScope.launch {
            delay(remaining)
            binding.lottieLoading.pauseAnimation()
            binding.overlayLoading.visibility = View.GONE
        }
    }
}