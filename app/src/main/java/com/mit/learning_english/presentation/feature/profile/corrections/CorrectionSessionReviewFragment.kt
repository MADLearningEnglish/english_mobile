package com.mit.learning_english.presentation.feature.profile.corrections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.mit.learning_english.databinding.FragmentCorrectionSessionReviewBinding
import com.mit.learning_english.databinding.ItemSessionImprovementBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CorrectionSessionReviewFragment : Fragment() {

    private var _binding: FragmentCorrectionSessionReviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CorrectionSessionReviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCorrectionSessionReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sessionId = arguments?.getInt("sessionId") ?: run {
            findNavController().navigateUp()
            return
        }

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        viewModel.load(sessionId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { review ->
                        if (review == null) return@collect
                        binding.tvContextHeader.text = review.contextHeader.orEmpty()
                        val datePart = CorrectionTimeFormatter.formatSessionDate(review.sessionStartedAt)
                        val mins = review.durationMinutes ?: 0
                        val cnt = review.improvementCount
                        binding.tvContextMeta.text =
                            getString(
                                com.mit.learning_english.R.string.session_review_meta_format,
                                datePart,
                                mins,
                                cnt
                            )
                        binding.layoutImprovements.removeAllViews()
                        val inflater = LayoutInflater.from(requireContext())
                        for (imp in review.improvements) {
                            val card = ItemSessionImprovementBinding.inflate(inflater, binding.layoutImprovements, false)
                            card.tvCategory.text = imp.category?.replace('_', ' ') ?: ""
                            card.tvExplanation.text = imp.explanation.orEmpty().ifBlank { "—" }
                            card.tvOriginal.text = imp.originalText.orEmpty().ifBlank { "—" }
                            card.tvCorrected.text = imp.suggestedText.orEmpty().ifBlank { "—" }
                            binding.layoutImprovements.addView(card.root)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
