package com.mit.learning_english.presentation.feature.bookdetail

import android.content.Intent
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
import com.mit.learning_english.shared.Constant
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
        viewModel.getBookDetail(args.bookId)
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
            layoutMenu.btnFavorite.setOnClickListener {
                viewModel.clickedFavorite()
            }
            layoutMenu.btnShare.setOnClickListener {
                shareBook()
            }
        }
    }

    private fun shareBook() {
        val state = viewModel.uiState.value
        val shareUrl = "${Constant.DEEP_LINK_HTTPS_BOOK_URL}${state.id}"
        val shareText = getString(R.string.share_book_message, state.title, shareUrl)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_book_title)))
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectStateProperty(viewModel.uiState, { it.title }) { title ->
            binding.tvBookTitle.text = title
            binding.tvBlurb.text = title
        }
        collectStateProperty(viewModel.uiState, { it.authorsName }) { authorsName ->
            binding.tvBookAuthor.text = authorsName
        }
        collectStateProperty(viewModel.uiState, { it.progressPercent }) { progressPercent ->
            binding.pbProgress.progress = progressPercent.toInt()
        }
        collectStateProperty(viewModel.uiState, { it.coverUrl }) { coverUrl ->
            if (coverUrl.isNotEmpty()) binding.ivBookCover.loadImage(coverUrl)
        }
        collectStateProperty(viewModel.uiState, { it.chapters }) { chapters ->
            if (chapters.isNotEmpty()) {
                chapterAdapter.submitList(chapters)
                binding.tvReadTimeAndPage.text = getString(R.string.minutes_format, chapters.sumOf { chapter -> chapter.totalDuration }/3600)
                binding.tvTotalPages.text = getString(R.string.total_page_format, chapters.sumOf { chapter -> chapter.totalPages })
            }
        }
        collectStateProperty(viewModel.uiState, { it.isFavorite }) { isFavorite ->
            if (!isFavorite) {
                binding.layoutMenu.icFavorite.setImageResource(R.drawable.ic_unfavorite)
            } else {
                binding.layoutMenu.icFavorite.setImageResource(R.drawable.ic_favorite)
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