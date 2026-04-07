package com.mit.learning_english.presentation.feature.booksbygenre

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentBookByGenreBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.utils.VerticalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookByGenreFragment : BaseFragment<FragmentBookByGenreBinding, BookByGenreViewModel>() {

    override val viewModel: BookByGenreViewModel by viewModels()
    private val args: BookByGenreFragmentArgs by navArgs()
    private lateinit var bookAdapter: BookByGenreAdapter

    override fun verifyBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBookByGenreBinding {
        return FragmentBookByGenreBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        bookAdapter = BookByGenreAdapter { book ->
            viewModel.onBookClicked(book.id)
        }
        binding.rvBooks.apply {
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalSpacingItemDecoration(12))
        }

        val title = args.genreName.takeIf { it.isNotBlank() }
            ?: getString(R.string.books_by_genre_default_title)
        binding.tvGenreName.text = title
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
                viewModel.books.collectLatest { pagingData ->
                    bookAdapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookAdapter.loadStateFlow.collectLatest { loadState ->
                    when (val refreshState = loadState.source.refresh) {
                        is LoadState.Loading -> {
                            binding.shimmerBooks.startShimmer()
                            binding.shimmerBooks.visibility = View.VISIBLE
                            binding.rvBooks.visibility = View.INVISIBLE
                        }

                        is LoadState.Error -> {
                            binding.shimmerBooks.stopShimmer()
                            binding.shimmerBooks.visibility = View.GONE
                            binding.rvBooks.visibility = View.VISIBLE
                            Toast.makeText(
                                requireContext(),
                                refreshState.error.localizedMessage
                                    ?: getString(R.string.failed_to_load_books),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("BookByGenre",refreshState.error.localizedMessage)
                        }

                        is LoadState.NotLoading -> {
                            binding.shimmerBooks.stopShimmer()
                            binding.shimmerBooks.visibility = View.GONE
                            binding.rvBooks.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        collectEvent(viewModel.event) { event ->
            when (event) {
                is BookByGenreEvent.NavigateToBookDetail -> {
                    val action =
                        BookByGenreFragmentDirections.actionBookByGenreFragmentToBookDetailFragment(
                            event.bookId
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }
}
