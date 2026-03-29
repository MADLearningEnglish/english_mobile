package com.mit.learning_english.presentation.feature.readbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.mit.learning_english.databinding.FragmentReadBookBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadBookFragment : BaseFragment<FragmentReadBookBinding, ReadBookViewModel>() {
    override val viewModel: ReadBookViewModel by viewModels()
    val args: ReadBookFragmentArgs by navArgs()
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
        binding.viewPager.offscreenPageLimit = 3

        chapterAdapter = ChapterAdapter { chapter ->
            binding.drawLayout.closeDrawers()
            viewModel.goToChapter(chapter.id)
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
        val readBookArgs = args.readBookArgs
        viewModel.loadInit(readBookArgs)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectState(viewModel.uiState) { state ->
            val newPageList = state.pages.values.toList()
            if (newPageList.size != pageAdapter.currentList.size || newPageList != pageAdapter.currentList) {
                pageAdapter.submitList(newPageList)
            }
            if (state.chapters != chapterAdapter.currentList) {
                chapterAdapter.submitList(state.chapters)
            }

            state.activeChapterId?.let {
                if (it != chapterAdapter.getActiveChapterId()) {
                    chapterAdapter.setActiveChapterId(it)
                }
            }
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
