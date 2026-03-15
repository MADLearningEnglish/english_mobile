package com.mit.learning_english.presentation.feature.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.databinding.FragmentHomeBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.feature.home.adapter.BookRecommendAdapter
import com.mit.learning_english.presentation.feature.home.adapter.GenreAdapter
import com.mit.learning_english.presentation.feature.home.adapter.BookHistoryAdapter
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override val viewModel: HomeViewModel by viewModels()
    private lateinit var recommendAdapter: BookRecommendAdapter
    private lateinit var genreAdapter: GenreAdapter
    private lateinit var recentBooksAdapter: BookHistoryAdapter

    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        recommendAdapter = BookRecommendAdapter()
        binding.rvRecommendBook.apply {
            adapter = recommendAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        genreAdapter = GenreAdapter()
        binding.rvGenres.apply {
            adapter = genreAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        recentBooksAdapter = BookHistoryAdapter()
        binding.rvRecentlyRead.apply {
            adapter = recentBooksAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun bindView() {
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectState(viewModel.uiState) { state ->
            if (state.isRecommendBooksLoading) {
                binding.shimmerRecommendBook.startShimmer()
                binding.shimmerRecommendBook.visibility = View.VISIBLE
                binding.rvRecommendBook.visibility = View.GONE
            } else {
                binding.shimmerRecommendBook.stopShimmer()
                binding.shimmerRecommendBook.visibility = View.GONE
                binding.rvRecommendBook.visibility = View.VISIBLE
                recommendAdapter.submitList(state.recommendBooks)
            }
            if (state.isGenresLoading) {
                binding.shimmerGenres.startShimmer()
                binding.shimmerGenres.visibility = View.VISIBLE
                binding.rvGenres.visibility = View.GONE
            } else {
                binding.shimmerGenres.stopShimmer()
                binding.shimmerGenres.visibility = View.GONE
                binding.rvGenres.visibility = View.VISIBLE
                genreAdapter.submitList(state.genres)
            }
        }
        collectState(viewModel.recentBooks) { pagingData ->
            recentBooksAdapter.submitData(lifecycle, pagingData)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            recentBooksAdapter.loadStateFlow.collectLatest { loadState ->
                if (loadState.source.refresh is LoadState.Loading) {
                    binding.shimmerRecentlyRead.startShimmer()
                    binding.shimmerRecentlyRead.visibility = View.VISIBLE
                    binding.rvRecentlyRead.visibility = View.GONE
                } else {
                    binding.shimmerRecentlyRead.stopShimmer()
                    binding.shimmerRecentlyRead.visibility = View.GONE
                    binding.rvRecentlyRead.visibility = View.VISIBLE
                }
            }
        }
    }

//    fun handleRecycleView(
//        state: HomeState,
//        shimmerFrameLayout: ShimmerFrameLayout,
//        recyclerView: RecyclerView,
//    ) {
//        if (state.isGenresLoading) {
//            shimmerFrameLayout.startShimmer()
//            shimmerFrameLayout.visibility = View.VISIBLE
//            recyclerView.visibility = View.GONE
//        } else {
//            shimmerFrameLayout.stopShimmer()
//            shimmerFrameLayout.visibility = View.GONE
//            recyclerView.visibility = View.VISIBLE
//            recommendAdapter.submitList(state.recommendBooks)
//        }
//    }
}