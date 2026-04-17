package com.mit.learning_english.presentation.feature.recommendbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.databinding.FragmentRecommendBookBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.utils.VerticalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecommendBookFragment :
    BaseFragment<FragmentRecommendBookBinding, RecommendBookViewModel>() {

    override val viewModel: RecommendBookViewModel by viewModels()
    private lateinit var recommendBookAdapter: RecommendBookPagingAdapter

    override fun verifyBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecommendBookBinding {
        return FragmentRecommendBookBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        recommendBookAdapter = RecommendBookPagingAdapter { book ->
            viewModel.onBookClick(book.id)
        }
        binding.rvRecommendBooks.apply {
            adapter = recommendBookAdapter
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
                viewModel.recommendBooks.collectLatest { pagingData ->
                    recommendBookAdapter.submitData(pagingData)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                recommendBookAdapter.loadStateFlow.collectLatest { loadState ->
                    when (val refreshState = loadState.source.refresh) {
                        is LoadState.Loading -> showLoading()
                        is LoadState.NotLoading -> {
                            hideLoading()
                            val isEmpty = loadState.source.append.endOfPaginationReached &&
                                recommendBookAdapter.itemCount == 0
                            binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
                        }
                        is LoadState.Error -> {
                            hideLoading()
                            binding.tvEmptyState.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                refreshState.error.localizedMessage ?: "Failed to load books",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        collectEvent(viewModel.event) { event ->
            when (event) {
                is RecommendBookEvent.NavigateToBookDetail -> {
                    val action =
                        RecommendBookFragmentDirections.actionRecommendBookFragmentToBookDetailFragment(
                            event.bookId
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun showLoading() {
//        binding.overlayLoading.visibility = View.VISIBLE
//        binding.lottieLoading.playAnimation()
    }

    override fun hideLoading() {
//        binding.lottieLoading.pauseAnimation()
//        binding.overlayLoading.visibility = View.GONE
    }
}
