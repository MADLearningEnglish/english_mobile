package com.mit.learning_english.presentation.feature.study

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentStudyBinding
import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudyFragment : BaseFragment<FragmentStudyBinding, StudyViewModel>() {

    override val viewModel: StudyViewModel by viewModels()

    private var mediaPlayer: MediaPlayer? = null
    private var outAnimator: android.animation.Animator? = null
    private var inAnimator: android.animation.Animator? = null

    override fun verifyBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStudyBinding = FragmentStudyBinding.inflate(inflater, container, false)

    override fun setupView() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }

    override fun bindView() {
        binding.btnBack.setOnClickListener {
            viewModel.onNavigateBack()
        }

        // Tap the card (front) to flip
        binding.cardFront.setOnClickListener {
            viewModel.flipCard()
        }

        // Tap the card (back) to flip back
        binding.cardBack.setOnClickListener {
            viewModel.flipCard()
        }
        binding.scrollCardBack.setOnClickListener {
            viewModel.flipCard()
        }
        binding.layoutCardBackContent.setOnClickListener {
            viewModel.flipCard()
        }

        // Speaker button on front card
        binding.btnSpeakerFront.setOnClickListener {
            speakWord()
        }

        // Single "Next" button
        binding.btnNextCard.setOnClickListener {
            lastIsFlipped = null
            viewModel.onNextCard()
        }

        // Complete screen back button
        binding.btnCompleteBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Empty state button
        binding.btnEmptyBack.setOnClickListener {
            viewModel.onNavigateBack()
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    renderState(state)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is StudyEvent.NavigateBack -> findNavController().popBackStack()
                        is StudyEvent.SessionComplete -> showCompleteState()
                    }
                }
            }
        }
    }

    // =================== State Rendering ===================

    private var lastIsFlipped: Boolean? = null

    private fun renderState(state: StudyState) {
        // Loading
        binding.progressLoading.visibility = if (state.isLoading) View.VISIBLE else View.GONE

        // Empty state
        if (!state.isLoading && state.flashcards.isEmpty() && !state.isComplete) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.cardContainer.visibility = View.GONE
            binding.btnNextCard.visibility = View.GONE
            binding.layoutHeader.visibility = View.GONE
            return
        } else {
            binding.layoutEmpty.visibility = View.GONE
            if (!state.isComplete) {
                binding.cardContainer.visibility = View.VISIBLE
                binding.btnNextCard.visibility = View.VISIBLE
                binding.layoutHeader.visibility = View.VISIBLE
            }
        }

        // Title + progress count
        binding.tvDeckTitle.text = state.deckTitle
        binding.tvProgress.text = state.progressText

        // Populate card data
        state.currentFlashcard?.let { bindFlashcard(it) }

        // Flip animation
        val wasFlipped = lastIsFlipped
        val isFlipped = state.isFlipped
        if (wasFlipped != null && wasFlipped != isFlipped) {
            if (isFlipped) {
                animateCardFlip(showBack = true)
            } else {
                animateCardFlip(showBack = false)
            }
        } else {
            outAnimator?.cancel()
            inAnimator?.cancel()

            if (!isFlipped) {
                binding.cardFront.animate().cancel()
                binding.cardBack.animate().cancel()
                binding.cardFront.visibility = View.VISIBLE
                binding.cardFront.alpha = 1f
                binding.cardFront.rotationY = 0f
                binding.cardBack.visibility = View.GONE
                binding.cardBack.alpha = 1f
                binding.cardBack.rotationY = 0f
            } else {
                binding.cardFront.animate().cancel()
                binding.cardBack.animate().cancel()
                binding.cardFront.visibility = View.GONE
                binding.cardBack.visibility = View.VISIBLE
                binding.cardBack.alpha = 1f
                binding.cardBack.rotationY = 0f
                binding.cardFront.alpha = 1f
                binding.cardFront.rotationY = 0f
            }
        }
        lastIsFlipped = isFlipped
    }

    private fun bindFlashcard(flashcard: Flashcard) {
        // Front
        binding.tvWordFront.text = flashcard.word
        binding.tvPhoneticFront.text = flashcard.phonetic ?: ""
        binding.tvPhoneticFront.visibility =
            if (flashcard.phonetic.isNullOrBlank()) View.GONE else View.VISIBLE

        // Back
        binding.tvWordBack.text = flashcard.word

        val pos = flashcard.partOfSpeech
        binding.tvPartOfSpeech.text = if (!pos.isNullOrBlank()) "($pos)" else ""
        binding.tvPartOfSpeech.visibility = if (!pos.isNullOrBlank()) View.VISIBLE else View.GONE

        binding.tvMeaning.text = flashcard.meaning

        // Example sentence
        val example = flashcard.exampleSentence
        if (!example.isNullOrBlank()) {
            binding.layoutExample.visibility = View.VISIBLE
            val highlighted = example.replace(
                flashcard.word,
                "<b><font color='#8C2BEE'>${flashcard.word}</font></b>",
                ignoreCase = true
            )
            binding.tvExampleSentence.text = Html.fromHtml(highlighted, Html.FROM_HTML_MODE_COMPACT)
        } else {
            binding.layoutExample.visibility = View.GONE
        }

        // Notes
        val note = flashcard.note
        if (!note.isNullOrBlank()) {
            binding.layoutNote.visibility = View.VISIBLE
            binding.tvNote.text = note
        } else {
            binding.layoutNote.visibility = View.GONE
        }

        // Visual cue image
        val imageUrl = flashcard.visualCueUrl
        if (!imageUrl.isNullOrBlank()) {
            binding.imgVisualCue.visibility = View.VISIBLE
            Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_hero_placeholder)
                .error(R.drawable.ic_hero_placeholder)
                .into(binding.imgVisualCue)
        } else {
            Glide.with(this)
                .load(R.drawable.ic_hero_placeholder)
                .centerCrop()
                .into(binding.imgVisualCue)
        }
    }

    // =================== Card Flip ===================

    @android.annotation.SuppressLint("ResourceType")
    private fun animateCardFlip(showBack: Boolean) {
        outAnimator?.cancel()
        inAnimator?.cancel()

        val outAnim = android.animation.AnimatorInflater.loadAnimator(
            requireContext(),
            if (showBack) R.animator.card_flip_left_out else R.animator.card_flip_right_out
        )
        val inAnim = android.animation.AnimatorInflater.loadAnimator(
            requireContext(),
            if (showBack) R.animator.card_flip_right_in else R.animator.card_flip_left_in
        )

        outAnimator = outAnim
        inAnimator = inAnim

        val viewToHide = if (showBack) binding.cardFront else binding.cardBack
        val viewToShow = if (showBack) binding.cardBack else binding.cardFront

        val distance = 8000 * resources.displayMetrics.density
        viewToHide.cameraDistance = distance
        viewToShow.cameraDistance = distance

        outAnim.setTarget(viewToHide)
        inAnim.setTarget(viewToShow)

        outAnim.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                viewToHide.visibility = View.GONE
                viewToShow.visibility = View.VISIBLE
                inAnim.start()
            }
        })

        outAnim.start()
    }

    // =================== Complete state ===================

    private fun showCompleteState() {
        val state = viewModel.uiState.value

        // Hide study UI
        binding.cardContainer.visibility = View.GONE
        binding.btnNextCard.visibility = View.GONE
        binding.layoutHeader.visibility = View.GONE

        // Show simple complete screen
        binding.tvCompleteSubtitle.text =
            "Bạn đã học hết ${state.totalCount} từ trong bộ thẻ này."
        binding.layoutComplete.visibility = View.VISIBLE
    }

    // =================== Audio ===================

    private fun speakWord() {
        val flashcard = viewModel.uiState.value.currentFlashcard ?: return
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) <= 0) return

        try {
            mediaPlayer?.reset()

            val encodedWord = Uri.encode(flashcard.word)
            val audioUrl = "https://dict.youdao.com/dictvoice?audio=$encodedWord&type=2"
            mediaPlayer?.setDataSource(audioUrl)
            mediaPlayer?.prepareAsync()
            mediaPlayer?.setOnPreparedListener { mp ->
                mp.start()
            }
            mediaPlayer?.setOnErrorListener { _, _, _ ->
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun showLoading() {
        binding.progressLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressLoading.visibility = View.GONE
    }

    override fun onDestroyView() {
        try {
            outAnimator?.cancel()
            inAnimator?.cancel()

            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
        outAnimator = null
        inAnimator = null
        super.onDestroyView()
    }
}
