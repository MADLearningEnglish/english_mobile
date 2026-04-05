package com.mit.learning_english.presentation.feature.study

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
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
        // ── Flashcard controls ──────────────────────────────────────────────
        binding.btnBack.setOnClickListener { viewModel.onNavigateBack() }

        binding.cardFront.setOnClickListener { viewModel.flipCard() }
        binding.cardBack.setOnClickListener { viewModel.flipCard() }
        binding.scrollCardBack.setOnClickListener { viewModel.flipCard() }
        binding.layoutCardBackContent.setOnClickListener { viewModel.flipCard() }

        binding.btnSpeakerFront.setOnClickListener { speakWord() }

        binding.btnNextCard.setOnClickListener {
            lastIsFlipped = null
            viewModel.onNextCard()
        }

        binding.btnCompleteBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnEmptyBack.setOnClickListener { viewModel.onNavigateBack() }

        // ── Quiz controls ───────────────────────────────────────────────────
        val choiceButtons = listOf(
            binding.btnChoice1, binding.btnChoice2, binding.btnChoice3, binding.btnChoice4
        )
        choiceButtons.forEach { btn ->
            btn.setOnClickListener { viewModel.onAnswerSelected(btn.text.toString()) }
        }

        binding.btnFillBlankSubmit.setOnClickListener {
            val input = binding.etFillBlank.text?.toString() ?: return@setOnClickListener
            if (input.isNotBlank()) {
                viewModel.onFillBlankSubmit(input)
                hideKeyboard()
            }
        }

        binding.etFillBlank.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val input = binding.etFillBlank.text?.toString() ?: ""
                if (input.isNotBlank()) {
                    viewModel.onFillBlankSubmit(input)
                    hideKeyboard()
                }
                true
            } else false
        }

        binding.btnQuizNext.setOnClickListener { viewModel.onQuizNext() }
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
            binding.btnNextCard.visibility = View.GONE
            binding.layoutHeader.visibility = View.GONE
            binding.layoutQuiz.visibility = View.GONE
            return
        } else {
            binding.layoutEmpty.visibility = View.GONE
        }

        // Quiz vs Flashcard mode
        if (state.studyMode == StudyMode.QUIZ) {
            renderQuizMode(state)
        } else {
            renderFlashcardMode(state)
        }

        // Header is always visible when there is content
        if (!state.isComplete) {
            binding.layoutHeader.visibility = View.VISIBLE
            binding.tvDeckTitle.text = state.deckTitle
            binding.tvProgress.text = state.progressText
        }
    }

    // ── Flashcard rendering ───────────────────────────────────────────────

    private fun renderFlashcardMode(state: StudyState) {
        binding.layoutQuiz.visibility = View.GONE
        binding.cardContainer.visibility = View.VISIBLE
        binding.btnNextCard.visibility = View.VISIBLE

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
        binding.tvWordFront.text = flashcard.word
        binding.tvPhoneticFront.text = flashcard.phonetic ?: ""
        binding.tvPhoneticFront.isVisible = !flashcard.phonetic.isNullOrBlank()

        binding.tvWordBack.text = flashcard.word
        val pos = flashcard.partOfSpeech
        binding.tvPartOfSpeech.text = if (!pos.isNullOrBlank()) "($pos)" else ""
        binding.tvPartOfSpeech.isVisible = !pos.isNullOrBlank()

        binding.tvMeaning.text = flashcard.meaning

        val example = flashcard.exampleSentence
        if (!example.isNullOrBlank()) {
            binding.layoutExample.visibility = View.VISIBLE
            val highlighted = example.replace(
                flashcard.word,
                "<b><font color='#8C2BEE'>${flashcard.word}</font></b>",
                ignoreCase = true
            )
            binding.tvExampleSentence.text =
                Html.fromHtml(highlighted, Html.FROM_HTML_MODE_COMPACT)
        } else {
            binding.layoutExample.visibility = View.GONE
        }

        val note = flashcard.note
        binding.layoutNote.isVisible = !note.isNullOrBlank()
        if (!note.isNullOrBlank()) binding.tvNote.text = note

        val imageUrl = flashcard.visualCueUrl
        if (!imageUrl.isNullOrBlank()) {
            binding.imgVisualCue.visibility = View.VISIBLE
            Glide.with(this).load(imageUrl).centerCrop()
                .placeholder(R.drawable.ic_hero_placeholder)
                .error(R.drawable.ic_hero_placeholder)
                .into(binding.imgVisualCue)
        } else {
            Glide.with(this).load(R.drawable.ic_hero_placeholder).centerCrop()
                .into(binding.imgVisualCue)
        }
    }

    // ── Quiz rendering ────────────────────────────────────────────────────

    private fun renderQuizMode(state: StudyState) {
        binding.cardContainer.visibility = View.GONE
        binding.btnNextCard.visibility = View.GONE
        binding.layoutQuiz.visibility = View.VISIBLE

        val question = state.currentQuestion ?: return

        // Badge
        binding.tvQuizTypeBadge.text = when (question.type) {
            QuizType.MEANING_TO_WORD -> "🔤  NGHĨA → TỪ"
            QuizType.WORD_TO_MEANING -> "📖  TỪ → NGHĨA"
            QuizType.FILL_BLANK      -> "✏️  ĐIỀN VÀO CHỖ TRỐNG"
        }

        // Prompt
        binding.tvQuizPrompt.text = when (question.type) {
            QuizType.MEANING_TO_WORD -> "Từ tiếng Anh nào có nghĩa là:\n\"${question.prompt}\""
            QuizType.WORD_TO_MEANING -> "Từ \"${question.prompt}\" có nghĩa là gì?"
            QuizType.FILL_BLANK      -> question.prompt
        }

        // Toggle MCQ / FillBlank
        if (question.type == QuizType.FILL_BLANK) {
            binding.layoutMcqChoices.visibility = View.GONE
            binding.layoutFillBlank.visibility = View.VISIBLE
            if (!state.isAnswerRevealed) {
                binding.etFillBlank.isEnabled = true
                binding.btnFillBlankSubmit.isEnabled = true
                binding.etFillBlank.text?.clear()
            } else {
                binding.etFillBlank.isEnabled = false
                binding.btnFillBlankSubmit.isEnabled = false
            }
        } else {
            binding.layoutFillBlank.visibility = View.GONE
            binding.layoutMcqChoices.visibility = View.VISIBLE
            bindMcqChoices(question, state.selectedAnswer, state.isAnswerRevealed)
        }

        // Feedback row
        if (state.isAnswerRevealed) {
            val isCorrect = when (question.type) {
                QuizType.FILL_BLANK -> state.selectedAnswer
                    ?.trim()?.equals(question.correctAnswer.trim(), ignoreCase = true) == true
                else -> state.selectedAnswer == question.correctAnswer
            }
            showFeedback(isCorrect, question.correctAnswer)
            binding.btnQuizNext.visibility = View.VISIBLE
        } else {
            binding.layoutQuizFeedback.visibility = View.GONE
            binding.btnQuizNext.visibility = View.GONE
        }
    }

    private fun bindMcqChoices(
        question: QuizQuestion,
        selectedAnswer: String?,
        isRevealed: Boolean
    ) {
        val buttons = listOf(
            binding.btnChoice1, binding.btnChoice2, binding.btnChoice3, binding.btnChoice4
        )
        question.choices.forEachIndexed { index, choice ->
            val btn = buttons.getOrNull(index) ?: return@forEachIndexed
            btn.visibility = View.VISIBLE
            btn.text = choice
            btn.isEnabled = !isRevealed

            // Reset styling
            btn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.body_primary))
            (btn as? MaterialButton)?.strokeColor =
                ContextCompat.getColorStateList(requireContext(), R.color.divider)

            if (isRevealed) {
                when {
                    choice == question.correctAnswer -> {
                        btn.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.quiz_correct)
                        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    choice == selectedAnswer -> {
                        btn.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.quiz_wrong)
                        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                }
            }
        }
        // Hide unused buttons (shouldn't happen with 4 choices)
        for (i in question.choices.size until buttons.size) {
            buttons[i].visibility = View.GONE
        }
    }

    private fun showFeedback(isCorrect: Boolean, correctAnswer: String) {
        binding.layoutQuizFeedback.visibility = View.VISIBLE
        if (isCorrect) {
            binding.imgFeedbackIcon.setImageResource(R.drawable.ic_lightbulb)
            binding.imgFeedbackIcon.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.quiz_correct)
            binding.tvFeedbackText.text = "✅ Chính xác!"
            binding.tvFeedbackText.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.quiz_correct))
        } else {
            binding.imgFeedbackIcon.setImageResource(R.drawable.ic_lightbulb)
            binding.imgFeedbackIcon.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.quiz_wrong)
            binding.tvFeedbackText.text = "❌ Đáp án đúng: $correctAnswer"
            binding.tvFeedbackText.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.quiz_wrong))
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
        val state = viewModel.uiState.value

        binding.cardContainer.visibility = View.GONE
        binding.btnNextCard.visibility = View.GONE
        binding.layoutHeader.visibility = View.GONE
        binding.layoutQuiz.visibility = View.GONE

        binding.tvCompleteSubtitle.text =
            "Bạn đã học hết ${state.totalCount} từ trong bộ thẻ này."

        if (state.quizTotal > 0) {
            binding.tvQuizScore.visibility = View.VISIBLE
            binding.tvQuizScore.text =
                "Kiểm tra: ${state.quizScore} / ${state.quizTotal} câu đúng 🎯"
        } else {
            binding.tvQuizScore.visibility = View.GONE
        }

        binding.layoutComplete.visibility = View.VISIBLE
    }

    // =================== Audio ===================

    private fun speakWord() {
        val flashcard = viewModel.uiState.value.currentFlashcard ?: return
        val audioManager =
            requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) <= 0) return
        try {
            mediaPlayer?.reset()
            val encodedWord = Uri.encode(flashcard.word)
            val audioUrl = "https://dict.youdao.com/dictvoice?audio=$encodedWord&type=2"
            mediaPlayer?.setDataSource(audioUrl)
            mediaPlayer?.prepareAsync()
            mediaPlayer?.setOnPreparedListener { mp -> mp.start() }
            mediaPlayer?.setOnErrorListener { _, _, _ -> true }
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
            mediaPlayer?.let { player ->
                if (player.isPlaying) player.stop()
                player.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null; outAnimator = null; inAnimator = null
        super.onDestroyView()
    }
}
