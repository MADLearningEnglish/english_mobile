package com.mit.learning_english.presentation.feature.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentHomeBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.feature.historyreadbook.HistoryReadBookPagingAdapter
import com.mit.learning_english.presentation.feature.home.adapter.AuthorAdapter
import com.mit.learning_english.presentation.feature.home.adapter.BookRecommendAdapter
import com.mit.learning_english.presentation.feature.home.adapter.FavoriteBookPagingAdapter
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
    private lateinit var authorAdapter: AuthorAdapter
    private lateinit var favoriteBooksAdapter: FavoriteBookPagingAdapter
    private lateinit var genreAdapter: GenreAdapter
    private lateinit var recentBooksAdapter: HistoryReadBookPagingAdapter

    private fun navigateFromMainGraph(direction: NavDirections) {
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(direction)
    }

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
        genreAdapter = GenreAdapter { genre ->
            viewModel.navigateToBookByGenre(genre.id, genre.name)
        }
        binding.rvGenres.apply {
            adapter = genreAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpacingItemDecoration(16))
        }
        authorAdapter = AuthorAdapter { author ->
            viewModel.navigateToDetailAuthor(author)
        }
        binding.rvAuthors.apply {
            adapter = authorAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpacingItemDecoration(16))
        }
        favoriteBooksAdapter = FavoriteBookPagingAdapter { book ->
            viewModel.navigateToBookDetail(book.id)
        }
        binding.rvFavoriteBooks.apply {
            adapter = favoriteBooksAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpacingItemDecoration(16))
        }
        recentBooksAdapter = HistoryReadBookPagingAdapter{book->
            viewModel.navigateToBookDetail(book.id)
        }
        binding.rvRecentlyRead.apply {
            adapter = recentBooksAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalSpacingItemDecoration(12))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchRecommendBooks()
        viewModel.fetchGenres()
        if (::authorAdapter.isInitialized) authorAdapter.refresh()
        if (::favoriteBooksAdapter.isInitialized) favoriteBooksAdapter.refresh()
        if (::recentBooksAdapter.isInitialized) recentBooksAdapter.refresh()
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
            tvViewAllRecommendBooks.setOnClickListener {
                viewModel.navigateToRecommendBooks()
            }
            tvViewAllRecentlyRead.setOnClickListener {
                viewModel.navigateToHistoryReadBooks()
            }
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
                    val action = MainFragmentDirections.actionMainFragmentToBookDetailFragment(bookId=event.bookId)
                    navigateFromMainGraph(action)
                }

                is HomeEvent.NavigateToHistoryReadBooks -> {
                    val action = MainFragmentDirections.actionMainFragmentToHistoryReadBookFragment()
                    navigateFromMainGraph(action)
                }

                is HomeEvent.NavigateToRecommentBookFragment -> {
                    val action = MainFragmentDirections.actionMainFragmentToRecommendBookFragment()
                    navigateFromMainGraph(action)
                }

                is HomeEvent.NavigateToSearchFragment -> {
                    val action = MainFragmentDirections.actionMainFragmentToSearchBookFragment()
                    navigateFromMainGraph(action)
                }

                is HomeEvent.NavigateToBookByGenre -> {
                    val action = MainFragmentDirections.actionMainFragmentToBookByGenreFragment(event.genreId, genreName = event.genreName)
                    navigateFromMainGraph(action)

                }

                is HomeEvent.NavigateToDetailAuthorFragment -> {
                    val action = MainFragmentDirections.actionMainFragmentToDetailAuthorFragment(authorId = event.authorId, authorName = event.authorName, authorAvatar = event.authorAvatar, authorNationality =  event.authorNationality, authorBiography = event.authorBiography)
                    navigateFromMainGraph(action)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authors.collectLatest { pagingData ->
                    authorAdapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authorAdapter.loadStateFlow.collectLatest { loadState ->
                    when (val refreshState = loadState.source.refresh) {
                        is LoadState.Loading -> {
                            binding.shimmerAuthors.startShimmer()
                            binding.shimmerAuthors.visibility = View.VISIBLE
                            binding.rvAuthors.visibility = View.INVISIBLE
                        }

                        is LoadState.Error -> {
                            binding.shimmerAuthors.stopShimmer()
                            binding.shimmerAuthors.visibility = View.INVISIBLE
                            binding.rvAuthors.visibility = View.VISIBLE
                            viewModel.setErrorMessage(
                                refreshState.error.localizedMessage ?: "Failed to load authors"
                            )
                        }

                        is LoadState.NotLoading -> {
                            binding.shimmerAuthors.stopShimmer()
                            binding.shimmerAuthors.visibility = View.INVISIBLE
                            binding.rvAuthors.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteBooks.collectLatest { pagingData ->
                    favoriteBooksAdapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                favoriteBooksAdapter.loadStateFlow.collectLatest { loadState ->
                    when (val refreshState = loadState.source.refresh) {
                        is LoadState.Loading -> {
                            binding.shimmerFavoriteBooks.startShimmer()
                            binding.shimmerFavoriteBooks.visibility = View.VISIBLE
                            binding.rvFavoriteBooks.visibility = View.INVISIBLE
                        }

                        is LoadState.Error -> {
                            binding.shimmerFavoriteBooks.stopShimmer()
                            binding.shimmerFavoriteBooks.visibility = View.INVISIBLE
                            binding.rvFavoriteBooks.visibility = View.VISIBLE
                            viewModel.setErrorMessage(
                                refreshState.error.localizedMessage ?: "Failed to load books"
                            )
                        }

                        is LoadState.NotLoading -> {
                            binding.shimmerFavoriteBooks.stopShimmer()
                            binding.shimmerFavoriteBooks.visibility = View.INVISIBLE
                            binding.rvFavoriteBooks.visibility = View.VISIBLE
                        }
                    }
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