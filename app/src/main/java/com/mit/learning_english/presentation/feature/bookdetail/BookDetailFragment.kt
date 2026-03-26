package com.mit.learning_english.presentation.feature.bookdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mit.learning_english.databinding.FragmentBookDetailBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDetailFragment : BaseFragment<FragmentBookDetailBinding, BookDetailViewModel>() {
    override val viewModel: BookDetailViewModel by viewModels()

    private lateinit var chapterAdapter: ChapterAdapter

    private val args: BookDetailFragmentArgs by navArgs()

    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentBookDetailBinding {
        return FragmentBookDetailBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        chapterAdapter = ChapterAdapter { chapter ->
            // Handle chapter click if needed
        }
        binding.rvChapter.apply {
            adapter = chapterAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun bindView() {
        viewModel.getBookDetail(args.bookId)

        binding.apply {
            btnReadBook.setOnClickListener {
                viewModel.navigateToReadBook(0)
            }
            btnListenBook.setOnClickListener {
                viewModel.navigateToReadBook(1)
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectState(viewModel.uiState) { state ->
            state.book?.let { book ->
                binding.apply {
                    tvBookTitle.text = book.title
                    tvBookAuthor.text = book.authorsName
                    // For tvBlurb, using title as fallback, should be actual description
                    tvBlurb.text = book.title 

                    Glide.with(requireContext())
                        .load(book.coverUrl)
                        .into(ivBookCover)

                    chapterAdapter.submitList(book.chapters)
                }
            }
        }
    }
}