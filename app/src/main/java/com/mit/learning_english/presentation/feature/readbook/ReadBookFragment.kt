package com.mit.learning_english.presentation.feature.readbook

import android.content.ComponentName
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentReadBookBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.service.AudioPlaybackService
import com.mit.learning_english.presentation.utils.VerticalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReadBookFragment : BaseFragment<FragmentReadBookBinding, ReadBookViewModel>() {
    override val viewModel: ReadBookViewModel by viewModels()
    val args: ReadBookFragmentArgs by navArgs()
    private lateinit var pageAdapter: ReadBookPageAdapter
    private lateinit var chapterAdapter: ChapterAdapter
    private var pendingScrollPosition: Int? = null

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var progressJob: Job? = null
    private var isSeeking = false

    private val speeds = floatArrayOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    private var currentSpeedIndex = 2

    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentReadBookBinding {
        return FragmentReadBookBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        pageAdapter = ReadBookPageAdapter()
        binding.viewPager.adapter = pageAdapter
        binding.viewPager.offscreenPageLimit = 2
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
                val page = pageAdapter.peek(position)
                viewModel.onPageAudioAvailable(page?.audio)
            }
        })

        pageAdapter.addOnPagesUpdatedListener {
            pendingScrollPosition?.let { pos ->
                binding.viewPager.setCurrentItem(pos, false)
                pendingScrollPosition = null
            }
        }

        setupBottomSheet()
        setupAudioControls()
    }

    private fun setupBottomSheet() {
        val bottomSheetView = binding.bottomSheet.root
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)
    }

    private fun setupAudioControls() {
        binding.bottomSheet.btnPlay.setOnClickListener {
            mediaController?.let { controller ->
                if (controller.isPlaying) controller.pause() else controller.play()
            }
        }

        binding.bottomSheet.btnPrev.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem > 0) {
                binding.viewPager.setCurrentItem(currentItem - 1, true)
            }
        }

        binding.bottomSheet.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < pageAdapter.itemCount - 1) {
                binding.viewPager.setCurrentItem(currentItem + 1, true)
            }
        }

        binding.bottomSheet.btnRewindBack5s.setOnClickListener {
            mediaController?.let { controller ->
                val newPos = (controller.currentPosition - 5000).coerceAtLeast(0)
                controller.seekTo(newPos)
            }
        }

        binding.bottomSheet.btnRewindNext5s.setOnClickListener {
            mediaController?.let { controller ->
                val newPos = (controller.currentPosition + 5000).coerceAtMost(controller.duration)
                controller.seekTo(newPos)
            }
        }

        binding.bottomSheet.tvSpeed.setOnClickListener {
            currentSpeedIndex = (currentSpeedIndex + 1) % speeds.size
            val speed = speeds[currentSpeedIndex]
            mediaController?.playbackParameters = PlaybackParameters(speed)
            binding.bottomSheet.tvSpeed.text = "${speed}x"
            viewModel.updatePlaybackSpeed(speed)
        }

        binding.bottomSheet.progressAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = mediaController?.duration ?: 0L
                    val position = (duration * progress / 1000L)
                    binding.bottomSheet.tvCurrentTime.text = formatTime(position)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress ?: 0
                val duration = mediaController?.duration ?: 0L
                val position = (duration * progress / 1000L)
                mediaController?.seekTo(position)
                isSeeking = false
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
                binding.btnReadMode.setImageResource(R.drawable.ic_reading_book)
            } else {
                showBottomSheet()
                binding.btnReadMode.setImageResource(R.drawable.ic_headphone)
            }
        }

        collectStateProperty(viewModel.uiState, { it.currentPageNumber }) { pageNumber ->
            binding.bottomSheet.tvNumberPageBS.text = getString(R.string.page_format,pageNumber)
        }

        collectEvent(viewModel.event) { event ->
            when (event) {
                is ReadBookEvent.GoToChapter -> {
                    pendingScrollPosition = event.index
                    if (pageAdapter.itemCount > event.index) {
                        binding.viewPager.setCurrentItem(event.index, false)
                    }
                }
                ReadBookEvent.ShareBook -> {}
                is ReadBookEvent.PlayAudio -> playAudio(event.url)
                ReadBookEvent.StopAudio -> stopAudio()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initializeMediaController()
    }

    override fun onStop() {
        super.onStop()
        progressJob?.cancel()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
    }

    private fun initializeMediaController() {
        val sessionToken = SessionToken(
            requireContext(),
            ComponentName(requireContext(), AudioPlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(requireContext(), sessionToken).buildAsync().also { future ->
            future.addListener({
                mediaController = future.get()
                setupPlayerListener()
                restoreStateIfNeeded()
            }, MoreExecutors.directExecutor())
        }
    }

    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlayPauseIcon(isPlaying)
                if (isPlaying) startProgressUpdates() else stopProgressUpdates()
                val controller = mediaController ?: return
                viewModel.updatePlaybackState(
                    isPlaying,
                    controller.currentPosition,
                    controller.duration.coerceAtLeast(0L)
                )
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val duration = mediaController?.duration ?: 0L
                    binding.bottomSheet.tvEndTime.text = formatTime(duration)
                    binding.bottomSheet.progressAudio.max = 1000
                }
            }
        })
    }

    private fun restoreStateIfNeeded() {
        val state = viewModel.uiState.value
        if (state.readMode == ReadMode.ListenMode && state.currentAudioUrl != null) {
            val controller = mediaController ?: return
            if (controller.mediaItemCount == 0) {
                playAudio(state.currentAudioUrl)
            } else {
                updatePlayPauseIcon(controller.isPlaying)
                if (controller.isPlaying) startProgressUpdates()
            }
        }
    }

    private fun playAudio(url: String) {
        val controller = mediaController ?: return
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(getString(R.string.page_format,viewModel.uiState.value.currentPageNumber))
                    .build()
            )
            .build()
        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()
    }

    private fun stopAudio() {
        mediaController?.let { controller ->
            controller.stop()
            controller.clearMediaItems()
        }
        stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                val controller = mediaController
                if (controller != null && controller.isPlaying && !isSeeking) {
                    val position = controller.currentPosition
                    val duration = controller.duration.coerceAtLeast(1L)
                    binding.bottomSheet.tvCurrentTime.text = formatTime(position)
                    binding.bottomSheet.progressAudio.progress = (position * 1000 / duration).toInt()
                    viewModel.updatePlaybackState(true, position, duration)
                }
                delay(500)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun updatePlayPauseIcon(isPlaying: Boolean) {
        binding.bottomSheet.icPlay.setImageResource(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
    }

    private fun showBottomSheet() {
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}
