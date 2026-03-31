package com.mit.learning_english.presentation.feature.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentHomeBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.feature.home.adapter.BookHistoryAdapter
import com.mit.learning_english.presentation.feature.home.adapter.BookRecommendAdapter
import com.mit.learning_english.presentation.feature.home.adapter.GenreAdapter
import com.mit.learning_english.presentation.feature.main.MainFragmentDirections
import com.mit.learning_english.presentation.utils.HorizontalSpacingItemDecoration
import com.mit.learning_english.presentation.utils.VerticalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

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
//        setupAppBarCollapseAnimation()

        recommendAdapter = BookRecommendAdapter { book -> viewModel.navigateToBookDetail(book.id) }
        binding.rvRecommendBook.apply {
            adapter = recommendAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpacingItemDecoration(16))
        }
        genreAdapter = GenreAdapter()
        binding.rvGenres.apply {
            adapter = genreAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpacingItemDecoration(16))
        }
        recentBooksAdapter = BookHistoryAdapter()
        binding.rvRecentlyRead.apply {
            adapter = recentBooksAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalSpacingItemDecoration(12))
        }
    }

    private fun setupAppBarCollapseAnimation() {
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            if (totalScrollRange == 0) return@OnOffsetChangedListener
            val collapseRatio = abs(verticalOffset).toFloat() / totalScrollRange
            val scale = 1f - collapseRatio
            val alpha = 1f - collapseRatio * 1.5f

            binding.layoutAppNameAvatar.apply {
                scaleX = scale.coerceIn(0.5f, 1f)
                scaleY = scale.coerceIn(0.5f, 1f)
                this.alpha = alpha.coerceAtLeast(0f)
                pivotX = 0f
                pivotY = height.toFloat() / 2
            }
        })
    }

    override fun bindView() {
        binding.apply {
            layoutSearch.setOnClickListener {
                viewModel.navigateToSearchBook()
            }
            btnSearchBook.setOnClickListener { viewModel.navigateToSearchBook() }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectStateProperty(
            viewModel.uiState,
            { Pair(it.isRecommendBooksLoading, it.recommendBooks) }
        ) { (isLoading, books) ->
            if (isLoading) {
                binding.shimmerRecommendBook.startShimmer()
                binding.shimmerRecommendBook.visibility = View.VISIBLE
                binding.rvRecommendBook.visibility = View.INVISIBLE
            } else {
                binding.shimmerRecommendBook.stopShimmer()
                binding.shimmerRecommendBook.visibility = View.INVISIBLE
                binding.rvRecommendBook.visibility = View.VISIBLE
                recommendAdapter.submitList(books)
            }
        }

        collectStateProperty(
            viewModel.uiState,
            { Pair(it.isGenresLoading, it.genres) }
        ) { (isLoading, genres) ->
            if (isLoading) {
                binding.shimmerGenres.startShimmer()
                binding.shimmerGenres.visibility = View.VISIBLE
                binding.rvGenres.visibility = View.INVISIBLE
            } else {
                binding.shimmerGenres.stopShimmer()
                binding.shimmerGenres.visibility = View.INVISIBLE
                binding.rvGenres.visibility = View.VISIBLE
                genreAdapter.submitList(genres)
            }
        }

        collectEvent(viewModel.event) { event ->
            when (event) {
                is HomeEvent.NavigateToBookDetailFragment -> {
                    val action =
                        MainFragmentDirections.actionMainFragmentToBookDetailFragment(event.bookId)
                    findNavController(
                        requireActivity(), R.id.nav_host_fragment
                    ).navigate(action)
                }

                is HomeEvent.NavigateToRecentlyReadBook -> {

                }

                is HomeEvent.NavigateToRecommentBookFragment -> {

                }

                is HomeEvent.NavigateToSearchFragment -> {
                    val action = MainFragmentDirections.actionMainFragmentToSearchBookFragment()
                    findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recentBooks.collectLatest { pagingData ->
                    recentBooksAdapter.submitData(pagingData)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                recentBooksAdapter.loadStateFlow.collectLatest { loadState ->
                    when (val refreshState = loadState.source.refresh) {
                        is LoadState.Loading -> {
                            binding.shimmerRecentlyRead.startShimmer()
                            binding.shimmerRecentlyRead.visibility = View.VISIBLE
                            binding.rvRecentlyRead.visibility = View.INVISIBLE
                        }

                        is LoadState.Error -> {
                            binding.shimmerRecentlyRead.stopShimmer()
                            binding.shimmerRecentlyRead.visibility = View.INVISIBLE
                            binding.rvRecentlyRead.visibility = View.VISIBLE
                            viewModel.setErrorMessage(
                                refreshState.error.localizedMessage ?: "Failed to load books"
                            )
                        }

                        is LoadState.NotLoading -> {
                            binding.shimmerRecentlyRead.stopShimmer()
                            binding.shimmerRecentlyRead.visibility = View.INVISIBLE
                            binding.rvRecentlyRead.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }


    }
}