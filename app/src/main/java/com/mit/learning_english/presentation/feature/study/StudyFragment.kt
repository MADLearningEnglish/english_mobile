package com.mit.learning_english.presentation.feature.study

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentStudyBinding
import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.domain.model.QuizQuestion
import com.mit.learning_english.domain.model.QuizType
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class StudyFragment : BaseFragment<FragmentStudyBinding, StudyViewModel>() {

    override val viewModel: StudyViewModel by viewModels()

    private var tts: TextToSpeech? = null
    private var outAnimator: android.animation.Animator? = null
    private var inAnimator: android.animation.Animator? = null

    override fun verifyBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStudyBinding = FragmentStudyBinding.inflate(inflater, container, false)

    override fun setupView() {
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(1.2f) // Tăng tốc độ đọc lên 1.2x
            }
        }
    }

    override fun bindView() {
        // ── Flashcard controls ──────────────────────────────────────────────
        binding.btnBack.setOnClickListener { viewModel.onNavigateBack() }

        binding.cardFront.setOnClickListener { viewModel.flipCard() }
        binding.cardBack.setOnClickListener { viewModel.flipCard() }
        binding.scrollCardBack.setOnClickListener { viewModel.flipCard() }
        binding.layoutCardBackContent.setOnClickListener { viewModel.flipCard() }

        binding.btnSpeakerFront.setOnClickListener { speakWord() }

        binding.btnPreviousCard.setOnClickListener {
            lastIsFlipped = null
            viewModel.onPreviousCard()
        }

        binding.btnNextCard.setOnClickListener {
            lastIsFlipped = null
            viewModel.onNextCard()
        }

        binding.btnEmptyBack.setOnClickListener { viewModel.onNavigateBack() }
    }

    override fun observeViewModel() {
        super.observeViewModel()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(viewModel.uiState, viewModel.isLoading) { state, isLoading ->
                    Pair(state, isLoading)
                }.collectLatest { (state, isLoading) -> renderState(state, isLoading) }
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

    private fun renderState(state: StudyState, isLoading: Boolean) {
        binding.progressLoading.isVisible = isLoading

        if (!isLoading && state.flashcards.isEmpty() && !state.isComplete) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.cardContainer.visibility = View.GONE
            binding.layoutBottom.visibility = View.GONE
            binding.layoutHeader.visibility = View.GONE
            return
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.layoutBottom.visibility = View.VISIBLE
        }

        // Quiz vs Flashcard mode
        renderFlashcardMode(state)

        // Header is always visible when there is content
        if (!state.isComplete) {
            binding.layoutHeader.visibility = View.VISIBLE
            binding.tvProgress.text = state.progressText
            
            // Cập nhật thanh tiến trình (progress bar)
            if (state.totalCount > 0) {
                val progress = ((state.currentIndex + 1).toFloat() / state.totalCount * 100).toInt()
                binding.progressBar.progress = progress
            }
        }
    }

    // ── Flashcard rendering ───────────────────────────────────────────────

    private fun renderFlashcardMode(state: StudyState) {
        binding.cardContainer.visibility = View.VISIBLE
        binding.layoutBottom.visibility = View.VISIBLE

        state.currentFlashcard?.let { bindFlashcard(it) }

        // Flip animation
        val wasFlipped = lastIsFlipped
        val isFlipped = state.isFlipped
        if (wasFlipped != null && wasFlipped != isFlipped) {
            animateCardFlip(showBack = isFlipped)
        } else {
            outAnimator?.cancel(); inAnimator?.cancel()
            if (!isFlipped) {
                binding.cardFront.visibility = View.VISIBLE
                binding.cardFront.alpha = 1f; binding.cardFront.rotationY = 0f
                binding.cardBack.visibility = View.GONE
                binding.cardBack.alpha = 1f; binding.cardBack.rotationY = 0f
            } else {
                binding.cardFront.visibility = View.GONE
                binding.cardBack.visibility = View.VISIBLE
                binding.cardBack.alpha = 1f; binding.cardBack.rotationY = 0f
                binding.cardFront.alpha = 1f; binding.cardFront.rotationY = 0f
            }
        }
        lastIsFlipped = isFlipped
    }

    private fun bindFlashcard(flashcard: Flashcard) {
        binding.tvWordFront.text = flashcard.term
        binding.tvMeaning.text = flashcard.definition

        val imageUrl = com.mit.learning_english.shared.MediaUrlResolver.resolve(flashcard.imageUrl)
        if (!imageUrl.isNullOrBlank()) {
            binding.imgVisualCue.visibility = View.VISIBLE
            Glide.with(this).load(imageUrl).centerInside()
                .placeholder(R.drawable.ic_hero_placeholder)
                .error(R.drawable.ic_hero_placeholder)
                .into(binding.imgVisualCue)
        } else {
            binding.imgVisualCue.visibility = View.GONE
        }
    }



    // =================== Card Flip ===================

    @android.annotation.SuppressLint("ResourceType")
    private fun animateCardFlip(showBack: Boolean) {
        outAnimator?.cancel(); inAnimator?.cancel()

        val outAnim = android.animation.AnimatorInflater.loadAnimator(
            requireContext(),
            if (showBack) R.animator.card_flip_left_out else R.animator.card_flip_right_out
        )
        val inAnim = android.animation.AnimatorInflater.loadAnimator(
            requireContext(),
            if (showBack) R.animator.card_flip_right_in else R.animator.card_flip_left_in
        )

        outAnimator = outAnim; inAnimator = inAnim

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

    // =================== Complete State ===================

    private fun showCompleteState() {
        viewModel.uiState.value
        findNavController().popBackStack()
        android.widget.Toast.makeText(requireContext(), "Đã ôn tập xong thẻ ghi nhớ!", android.widget.Toast.LENGTH_SHORT).show()
    }

    // =================== Audio ===================

    private fun speakWord() {
        val flashcard = viewModel.uiState.value.currentFlashcard ?: return
        val audioManager =
            requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) <= 0) return
        try {
            tts?.speak(flashcard.term, TextToSpeech.QUEUE_FLUSH, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    // =================== Lifecycle ===================

    override fun showLoading() {
        binding.progressLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressLoading.visibility = View.GONE
    }

    override fun onDestroyView() {
        try {
            outAnimator?.cancel(); inAnimator?.cancel()
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tts = null; outAnimator = null; inAnimator = null
        super.onDestroyView()
    }
}
