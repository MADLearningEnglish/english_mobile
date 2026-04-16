package com.mit.learning_english.presentation.feature.profile.corrections

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemCorrectionHeaderBinding
import com.mit.learning_english.databinding.ItemMyCorrectionCardBinding

class MyCorrectionsRowAdapter(
    private val onReviewRule: (sessionId: Int) -> Unit,
    private val onSpeakCorrected: (text: String) -> Unit,
    private val relativeTime: (occurredAtEpochMs: Long?, occurredAt: String?) -> String
) : PagingDataAdapter<CorrectionRow, RecyclerView.ViewHolder>(DIFF) {

    fun refreshTimes() {
        // Rebind visible items so relative "x giây trước" stays accurate.
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is CorrectionRow.Header -> VT_HEADER
            is CorrectionRow.EntryRow -> VT_ENTRY
            null -> VT_ENTRY
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VT_HEADER) {
            HeaderVH(ItemCorrectionHeaderBinding.inflate(inflater, parent, false))
        } else {
            EntryVH(
                ItemMyCorrectionCardBinding.inflate(inflater, parent, false),
                onReviewRule,
                onSpeakCorrected,
                relativeTime
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = getItem(position)) {
            is CorrectionRow.Header -> (holder as HeaderVH).bind(row.title)
            is CorrectionRow.EntryRow -> (holder as EntryVH).bind(row.item)
            null -> {}
        }
    }

    class HeaderVH(private val binding: ItemCorrectionHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.tvHeader.text = title
        }
    }

    class EntryVH(
        private val binding: ItemMyCorrectionCardBinding,
        private val onReviewRule: (Int) -> Unit,
        private val onSpeakCorrected: (String) -> Unit,
        private val relativeTime: (Long?, String?) -> String
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: com.mit.learning_english.domain.model.profile.UserCorrectionItem) {
            binding.tvRelative.text = relativeTime(item.occurredAtEpochMs, item.occurredAt)
            binding.tvErrorType.text = item.errorType?.replace('_', ' ')?.replaceFirstChar { it.titlecase() }
                ?: "Correction"
            binding.tvOriginal.text = item.originalText.orEmpty().ifBlank { "—" }
            binding.tvCorrected.text = item.suggestedText.orEmpty().ifBlank { "—" }
            binding.tvSource.text = item.sourceLabel.orEmpty()
            binding.btnReviewRule.setOnClickListener { onReviewRule(item.sessionId) }
            binding.btnSpeak.setOnClickListener {
                val t = item.suggestedText?.trim().orEmpty()
                if (t.isNotEmpty()) onSpeakCorrected(t)
            }
        }
    }

    companion object {
        private const val VT_HEADER = 0
        private const val VT_ENTRY = 1

        private val DIFF = object : DiffUtil.ItemCallback<CorrectionRow>() {
            override fun areItemsTheSame(a: CorrectionRow, b: CorrectionRow): Boolean = when {
                a is CorrectionRow.Header && b is CorrectionRow.Header -> a.title == b.title
                a is CorrectionRow.EntryRow && b is CorrectionRow.EntryRow -> a.item.errorId == b.item.errorId
                else -> false
            }

            override fun areContentsTheSame(a: CorrectionRow, b: CorrectionRow): Boolean = a == b
        }
    }
}
