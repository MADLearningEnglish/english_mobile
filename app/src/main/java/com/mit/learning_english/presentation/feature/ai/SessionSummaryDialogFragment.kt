package com.mit.learning_english.presentation.feature.ai

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentSessionSummaryBinding
import com.mit.learning_english.databinding.ItemSessionSuggestionBinding
import com.mit.learning_english.presentation.feature.ai.model.SessionSummaryArgs

class SessionSummaryDialogFragment : androidx.fragment.app.DialogFragment() {

    private var _binding: FragmentSessionSummaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentSessionSummaryBinding.inflate(layoutInflater)
        populate(binding, requireArguments().args())
        binding.btnClose.setOnClickListener {
            parentFragmentManager.setFragmentResult(REQUEST_SUMMARY_CLOSED, bundleOf())
            dismiss()
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create().also { d ->
                d.setOnShowListener {
                    val w = d.window ?: return@setOnShowListener
                    w.setBackgroundDrawableResource(android.R.color.transparent)
                    val width = (resources.displayMetrics.widthPixels * 0.92f).toInt()
                    w.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
                    w.setDimAmount(0.48f)
                }
            }
    }

    override fun onCancel(dialog: DialogInterface) {
        parentFragmentManager.setFragmentResult(REQUEST_SUMMARY_CLOSED, bundleOf())
        super.onCancel(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Bundle.args(): SessionSummaryArgs {
        @Suppress("DEPRECATION")
        return getParcelable(ARG_ARGS)!!
    }

    private fun populate(b: FragmentSessionSummaryBinding, args: SessionSummaryArgs) {
        b.textDuration.text = getString(R.string.ai_summary_duration_format, args.durationMinutes)
        b.textFluency.text = formatSummaryLevel(args.fluencyLevel)
        b.textGrammar.text = formatSummaryLevel(args.grammarLevel)
        b.textVocabulary.text = formatSummaryLevel(args.vocabularyLevel)
        b.textSentences.text = args.sentenceCount.toString()
        b.textErrors.text = args.errorCount.toString()
        b.containerSuggestions.removeAllViews()
        if (args.nextSuggestions.isEmpty()) {
            b.containerSuggestions.visibility = View.GONE
            b.textNextTopicsHeader.visibility = View.GONE
        } else {
            b.textNextTopicsHeader.visibility = View.VISIBLE
            b.containerSuggestions.visibility = View.VISIBLE
            args.nextSuggestions.forEachIndexed { index, text ->
                val row = ItemSessionSuggestionBinding.inflate(layoutInflater, b.containerSuggestions, false)
                row.textSuggestion.text = text
                row.iconSuggestion.setImageResource(
                    if (index % 2 == 0) R.drawable.ic_restaurant_topic else R.drawable.ic_briefcase_topic,
                )
                row.root.setOnClickListener {
                    parentFragmentManager.setFragmentResult(
                        REQUEST_NEXT_TOPIC,
                        bundleOf(KEY_NEXT_TOPIC to text),
                    )
                    dismiss()
                }
                b.containerSuggestions.addView(row.root)
            }
        }
    }

    /** Backend gửi GOOD / FAIR / NEED_IMPROVEMENT — hiển thị kiểu mockup (Good, Fair, …). */
    private fun formatSummaryLevel(raw: String?): String {
        val s = raw?.trim().orEmpty()
        if (s.isEmpty()) return "—"
        return when (s.uppercase()) {
            "GOOD" -> "Good"
            "FAIR" -> "Fair"
            "NEED_IMPROVEMENT" -> "Improving"
            "EXCELLENT" -> "Excellent"
            else -> s.lowercase().replaceFirstChar { c -> c.titlecase() }
        }
    }

    companion object {
        const val REQUEST_SUMMARY_CLOSED = "ai_session_summary_closed"
        const val REQUEST_NEXT_TOPIC = "ai_session_next_topic"
        const val KEY_NEXT_TOPIC = "next_topic"
        private const val ARG_ARGS = "args"

        fun newInstance(args: SessionSummaryArgs): SessionSummaryDialogFragment {
            return SessionSummaryDialogFragment().apply {
                arguments = bundleOf(ARG_ARGS to args)
            }
        }
    }
}
