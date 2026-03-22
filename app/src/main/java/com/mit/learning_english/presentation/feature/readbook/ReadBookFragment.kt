package com.mit.learning_english.presentation.feature.readbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.mit.learning_english.databinding.FragmentReadBookBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadBookFragment : BaseFragment<FragmentReadBookBinding, ReadBookViewModel>() {
    override val viewModel: ReadBookViewModel by viewModels()
    private lateinit var pageAdapter: ReadBookPageAdapter
    private lateinit var chapterAdapter: ChapterAdapter

    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentReadBookBinding {
        return FragmentReadBookBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        pageAdapter = ReadBookPageAdapter()
        binding.viewPager.adapter = pageAdapter

        chapterAdapter = ChapterAdapter { chapter ->
            binding.drawLayout.closeDrawers()
            viewModel.goToChapter(chapter)
        }
        binding.rvChapters.apply {
            adapter = chapterAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.onPageChanged(position)
            }
        })
    }

    override fun bindView() {
        val bookId: Int = arguments?.getInt("bookId", -1) ?: -1
        val pageNumberLastRead: Int = arguments?.getInt("pageNumberLastRead", 0) ?: 0
        viewModel.loadInitialPages(bookId, pageNumberLastRead)
        viewModel.loadInitChapters(bookId, pageNumberLastRead)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectState(viewModel.uiState) { state ->
            pageAdapter.submitList(state.pages.values.toList())
            chapterAdapter.submitList(state.chapters)
            state.activeChapterId?.let { chapterAdapter.setActiveChapterId(it) }
        }
        collectEvent(viewModel.event) { event ->
            when (event) {
                is ReadBookEvent.GoToChapter -> {
                    binding.viewPager.currentItem = event.index
                }

                ReadBookEvent.ShareBook -> {

                }

                ReadBookEvent.ShowTabBar -> {

                }
            }
        }
    }
}
