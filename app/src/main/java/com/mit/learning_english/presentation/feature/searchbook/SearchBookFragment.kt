package com.mit.learning_english.presentation.feature.searchbook

import android.text.Editable
import android.text.TextWatcher
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
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentSearchBookBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.utils.VerticalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchBookFragment : BaseFragment<FragmentSearchBookBinding, SearchBookViewModel>() {

    override val viewModel: SearchBookViewModel by viewModels()
    private lateinit var searchBookAdapter: SearchBookAdapter

    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSearchBookBinding {
        return FragmentSearchBookBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        searchBookAdapter = SearchBookAdapter { book ->
            viewModel.onBookClicked(book.id)
        }
        binding.rvSearchResult.apply {
            adapter = searchBookAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalSpacingItemDecoration(12))
        }
    }

    override fun bindView() {
        binding.edtKeyWordSearchBook.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString().orEmpty()
                viewModel.onSearchQueryChanged(query)
                binding.btnClearSearch.visibility =
                    if (query.isNotEmpty()) View.VISIBLE else View.GONE
            }
        })

        binding.btnClearSearch.setOnClickListener {
            binding.edtKeyWordSearchBook.text?.clear()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSearch.setOnClickListener {
            val query = binding.edtKeyWordSearchBook.text?.toString().orEmpty()
            if (query.isNotBlank()) {
                viewModel.onSearchQueryChanged(query)
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResults.collectLatest { pagingData ->
                    searchBookAdapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchBookAdapter.loadStateFlow.collectLatest { loadState ->
                    when (val refreshState = loadState.source.refresh) {
                        is LoadState.Loading -> {
                            binding.shimmerSearchBook.startShimmer()
                            binding.shimmerSearchBook.visibility = View.VISIBLE
                            binding.rvSearchResult.visibility = View.INVISIBLE
                        }

                        is LoadState.Error -> {
                            binding.shimmerSearchBook.stopShimmer()
                            binding.shimmerSearchBook.visibility = View.GONE
                            binding.rvSearchResult.visibility = View.VISIBLE
                            Toast.makeText(
                                requireContext(),
                                refreshState.error.localizedMessage ?: getString(R.string.error_failed_search_books),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is LoadState.NotLoading -> {
                            binding.shimmerSearchBook.stopShimmer()
                            binding.shimmerSearchBook.visibility = View.GONE
                            binding.rvSearchResult.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        collectEvent(viewModel.event) { event ->
            when (event) {
                is SearchBookEvent.NavigateToBookDetail -> {
                    // TODO: navigate to book detail
                }
            }
        }
    }
}
