package com.mit.learning_english.presentation.feature.readbook

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.mit.learning_english.databinding.FragmentReadBookBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.utils.VerticalSpacingItemDecoration
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
            addItemDecoration(VerticalSpacingItemDecoration(12))
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
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnShowNavView.setOnClickListener {
            binding.drawLayout.openDrawer(GravityCompat.END)
        }
        binding.btnReadMode.setOnClickListener {
            viewModel.readModeClicked()
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectStateProperty(viewModel.uiState, { it.pages }) { pages ->
            pageAdapter.submitList(pages.values.toList())
        }

        collectStateProperty(viewModel.uiState, { it.chapters }) { chapters ->
            chapterAdapter.submitList(chapters)
        }

        collectStateProperty(
            viewModel.uiState,
            { Pair(it.activeChapterId, it.chapters) }
        ) { (activeChapterId, chapters) ->
            activeChapterId?.let { id ->
                chapterAdapter.setActiveChapterId(id)
                binding.tvTitleChapter.text =
                    chapters.find { it.id == id }?.title ?: ""
            }
        }
        collectStateProperty(viewModel.uiState,{it.readMode}){readMode ->
          if(readMode == ReadMode.ReadMode){
              hideBottomSheet()
          }else{
              showBottomSheet()
          }

        }
        collectEvent(viewModel.event) { event ->
            when (event) {
                is ReadBookEvent.GoToChapter -> {
                    binding.viewPager.currentItem = event.index
                }

                ReadBookEvent.ShareBook -> {

                }


            }
        }
    }

    private fun showBottomSheet() {

    }
    private fun hideBottomSheet() {

    }

}
