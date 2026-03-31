package com.mit.learning_english.presentation.feature.readbook

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.mit.learning_english.databinding.FragmentReadBookBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.utils.VerticalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pagesFlow.collectLatest { pagingData ->
                    pageAdapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pageAdapter.loadStateFlow.collectLatest { loadStates ->
                    val errorState = loadStates.refresh as? LoadState.Error
                        ?: loadStates.append as? LoadState.Error
                        ?: loadStates.prepend as? LoadState.Error
                    errorState?.let {
                        Toast.makeText(
                            requireContext(),
                            it.error.localizedMessage ?: "Failed to load pages",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
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

        collectStateProperty(viewModel.uiState, { it.readMode }) { readMode ->
            if (readMode == ReadMode.ReadMode) {
                hideBottomSheet()
            } else {
                showBottomSheet()
            }
        }

        collectEvent(viewModel.event) { event ->
            when (event) {
                is ReadBookEvent.GoToChapter -> {
                    binding.viewPager.setCurrentItem(event.index, false)
                }

                ReadBookEvent.ShareBook -> {}
            }
        }
    }

    private fun showBottomSheet() {
    }

    private fun hideBottomSheet() {
    }
}
