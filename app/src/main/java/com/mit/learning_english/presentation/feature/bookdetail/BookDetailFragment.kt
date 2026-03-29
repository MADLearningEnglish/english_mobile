package com.mit.learning_english.presentation.feature.bookdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentBookDetailBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.extensions.loadImage
import com.mit.learning_english.presentation.utils.VerticalSpacingItemDecoration
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
            addItemDecoration(VerticalSpacingItemDecoration(12))
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
            btnBack.setOnClickListener {
                findNavController().navigateUp()
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
                    pbProgress.progress = book.progressPercent.toInt()
                    tvBlurb.text = book.title
                    ivBookCover.loadImage(book.coverUrl)
                    chapterAdapter.submitList(book.chapters)
                    tvReadTime.text = getString(R.string.minutes_format, book.chapters.sumOf { chapter-> chapter.totalDuration })
                    tvTotalPages.text = getString(R.string.total_page_format,book.chapters.sumOf { chapter ->  chapter.totalPages })
                }
            }
        }
        collectEvent(viewModel.event){event->
            when(event){
                is BookDetailEvent.NavigateToReadBook ->{
                    val action = BookDetailFragmentDirections.actionBookDetailFragmentToReadBookFragment(event.readBookArgs)
                    findNavController().navigate(action)
                }
            }
        }
    }
}