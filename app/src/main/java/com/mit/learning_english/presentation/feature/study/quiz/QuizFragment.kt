package com.mit.learning_english.presentation.feature.study.quiz

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.mit.learning_english.databinding.FragmentQuizBinding
import com.mit.learning_english.domain.model.QuizQuestion
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.shared.MediaUrlResolver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuizFragment : BaseFragment<FragmentQuizBinding, QuizViewModel>() {

    override val viewModel: QuizViewModel by viewModels()

    override fun verifyBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentQuizBinding = FragmentQuizBinding.inflate(inflater, container, false)

    override fun setupView() {
    }

    override fun bindView() {
        binding.btnBack.setOnClickListener { viewModel.onNavigateBack() }
        
        val choiceButtons = listOf(
            binding.btnChoice1, binding.btnChoice2, binding.btnChoice3, binding.btnChoice4
        )
        choiceButtons.forEach { btn ->
            btn.setOnClickListener { viewModel.onAnswerSelected(btn.text.toString()) }
        }

        binding.btnCompleteBack.setOnClickListener {
            findNavController().popBackStack()
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
                viewModel.isLoading.collectLatest { isLoading ->
                    binding.progressLoading.isVisible = isLoading
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is QuizEvent.NavigateBack -> findNavController().popBackStack()
                        is QuizEvent.SessionComplete -> showCompleteState()
                    }
                }
            }
        }
    }

    private fun renderState(state: QuizState) {
        if (state.isComplete) return
        
        binding.layoutHeader.visibility = View.VISIBLE
        
        // Setup Progress Bar
        binding.tvCurrentProgress.text = (state.currentIndex + 1).toString()
        binding.tvTotalScore.text = state.totalCount.toString()
        
        if (state.totalCount > 0) {
            val progress = ((state.currentIndex + 1).toFloat() / state.totalCount * 100).toInt()
            binding.progressBar.progress = progress
        }

        val question = state.currentQuestion ?: return
        binding.tvPrompt.text = question.prompt

        // Image Handling
        val flashcard = state.flashcards.find { it.id == question.sourceFlashcardId }
        val rawUrl = flashcard?.imageUrl
        val imageUrl = MediaUrlResolver.resolve(rawUrl)
        if (!imageUrl.isNullOrBlank()) {
            binding.imgVisualCue.visibility = View.VISIBLE
            Glide.with(this).load(imageUrl).centerInside()
                .placeholder(R.drawable.ic_hero_placeholder)
                .error(R.drawable.ic_hero_placeholder)
                .into(binding.imgVisualCue)
        } else {
            binding.imgVisualCue.visibility = View.GONE
        }

        bindMcqChoices(question, state.selectedAnswer, state.isAnswerRevealed)

        // Automatically next after 1.5 seconds if answer is revealed
        if (state.isAnswerRevealed) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    viewModel.onNextQuestion()
                }
            }, 1000)
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
        for (i in question.choices.size until buttons.size) {
            buttons[i].visibility = View.GONE
        }
    }

    private fun showCompleteState() {
        val state = viewModel.uiState.value
        binding.layoutHeader.visibility = View.GONE
        binding.tvCompleteSubtitle.text = "Bạn đã hoàn thành bài học ${state.totalCount} từ.\nSố câu đúng: ${state.correctCount}/${state.totalCount}."
        binding.layoutComplete.visibility = View.VISIBLE
    }
}
