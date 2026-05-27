package com.mit.learning_english.presentation.feature.detailauthor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentDetailAuthorBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.extensions.loadAvatar
import com.mit.learning_english.presentation.feature.recommendbook.RecommendBookPagingAdapter
import com.mit.learning_english.presentation.utils.VerticalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
/**
 * Màn hình hiển thị thông tin tác giả và các sách liên quan.
 */
class DetailAuthorFragment : BaseFragment<FragmentDetailAuthorBinding, DetailAuthorViewModel>() {
    override val viewModel: DetailAuthorViewModel by viewModels()

    private lateinit var authorBooksAdapter: RecommendBookPagingAdapter

    /**
     * Khởi tạo binding cho layout của fragment.
     */
    override fun verifyBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailAuthorBinding {
        return FragmentDetailAuthorBinding.inflate(inflater, container, false)
    }

    /**
     * Thiết lập RecyclerView hiển thị danh sách sách của tác giả.
     */
    override fun setupView() {
        authorBooksAdapter = RecommendBookPagingAdapter { book ->
            viewModel.onBookClick(book.id)
        }
        binding.rvAuthorBooks.apply {
            adapter = authorBooksAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalSpacingItemDecoration(12))
        }
    }

    /**
     * Gán hành vi cho nút quay lại.
     */
    override fun bindView() {
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
    }

    /**
     * Theo dõi state/event để cập nhật UI và điều hướng.
     */
    override fun observeViewModel() {
        super.observeViewModel()
        collectStateProperty(viewModel.uiState, { it.authorName }) { name ->
            binding.tvAuthorName.text = name
        }
        collectStateProperty(viewModel.uiState, { it.authorNationality }) { nationality ->
            binding.tvAuthorNationality.text = nationality
        }
        collectStateProperty(viewModel.uiState, { it.authorBiography }) { biography ->
            binding.tvAuthorBiography.text = biography
        }
        collectStateProperty(viewModel.uiState, { it.authorAvatar }) { avatar ->
            binding.ivAuthorAvatar.loadAvatar(avatar, binding.shimmerAvatar)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authorBooks.collectLatest { pagingData ->
                    authorBooksAdapter.submitData(pagingData)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authorBooksAdapter.loadStateFlow.collectLatest { loadState ->
                    when (val refreshState = loadState.source.refresh) {
                        is LoadState.Loading -> showLoading()
                        is LoadState.NotLoading -> {
                            hideLoading()
                            val isEmpty = loadState.source.append.endOfPaginationReached &&
                                authorBooksAdapter.itemCount == 0
                            binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
                        }

                        is LoadState.Error -> {
                            hideLoading()
                            binding.tvEmptyState.visibility = View.GONE
                            viewModel.setErrorMessage(
                                refreshState.error.localizedMessage ?: getString(R.string.error_failed_load_books)
                            )
                        }
                    }
                }
            }
        }

        collectEvent(viewModel.event) { event ->
            when (event) {
                is DetailAuthorEvent.NavigateToBookDetail -> {
                    val action =
                        DetailAuthorFragmentDirections.actionDetailAuthorFragmentToBookDetailFragment(
                            event.bookId
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }

    /**
     * Hiển thị overlay loading.
     */
    override fun showLoading() {
        binding.overlayLoading.visibility = View.VISIBLE
        binding.lottieLoading.playAnimation()
    }

    /**
     * Ẩn overlay loading.
     */
    override fun hideLoading() {
        binding.lottieLoading.pauseAnimation()
        binding.overlayLoading.visibility = View.GONE
    }
}