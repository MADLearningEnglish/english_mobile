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
            viewModel.navigateToReadBook(readMode = 0, chapterId = chapter.id)
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
        collectStateProperty(viewModel.uiState, { it.book }) { book ->
            book?.let {
                binding.apply {
                    tvBookTitle.text = it.title
                    tvBookAuthor.text = it.authorsName
                    pbProgress.progress = it.progressPercent.toInt()
                    tvBlurb.text = it.title
                    ivBookCover.loadImage(it.coverUrl)
                    chapterAdapter.submitList(it.chapters)
                    tvReadTime.text = getString(R.string.minutes_format, it.chapters.sumOf { chapter -> chapter.totalDuration })
                    tvTotalPages.text = getString(R.string.total_page_format, it.chapters.sumOf { chapter -> chapter.totalPages })
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