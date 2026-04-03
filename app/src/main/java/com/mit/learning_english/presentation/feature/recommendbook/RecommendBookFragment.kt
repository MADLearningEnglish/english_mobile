package com.mit.learning_english.presentation.feature.recommendbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.databinding.FragmentRecommendBookBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.feature.home.adapter.BookRecommendAdapter
import com.mit.learning_english.presentation.utils.HorizontalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendBookFragment :
    BaseFragment<FragmentRecommendBookBinding, RecommendBookViewModel>() {

    override val viewModel: RecommendBookViewModel by viewModels()
    private lateinit var topicAdapter: BookRecommendAdapter
    private lateinit var authorAdapter: BookRecommendAdapter
    private lateinit var inProgressAdapter: ReadingInProgressHorizontalAdapter

    override fun verifyBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecommendBookBinding {
        return FragmentRecommendBookBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        topicAdapter = BookRecommendAdapter { book ->
            viewModel.onBookClick(book.id)
        }
        authorAdapter = BookRecommendAdapter { book ->
            viewModel.onBookClick(book.id)
        }
        inProgressAdapter = ReadingInProgressHorizontalAdapter { book ->
            viewModel.onBookClick(book.id)
        }

        binding.rvRecommendByTopic.apply {
            adapter = topicAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpacingItemDecoration(16))
        }
        binding.rvRecommendByAuthor.apply {
            adapter = authorAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpacingItemDecoration(16))
        }
        binding.rvReadingInProgress.apply {
            adapter = inProgressAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpacingItemDecoration(16))
        }
    }

    override fun bindView() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectState(viewModel.uiState) { state ->
            topicAdapter.submitList(state.booksByTopic)
            authorAdapter.submitList(state.booksByAuthor)
            inProgressAdapter.submitList(state.booksInProgress)
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
        binding.overlayLoading.visibility = View.VISIBLE
        binding.lottieLoading.playAnimation()
    }

    override fun hideLoading() {
        binding.lottieLoading.pauseAnimation()
        binding.overlayLoading.visibility = View.GONE
    }
}
