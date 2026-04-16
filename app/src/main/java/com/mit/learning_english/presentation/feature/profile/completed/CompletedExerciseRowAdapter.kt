package com.mit.learning_english.presentation.feature.profile.completed

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemCompletedExerciseRowBinding
import com.mit.learning_english.domain.model.profile.LearningActivityItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class CompletedExerciseRowAdapter :
    PagingDataAdapter<LearningActivityItem, CompletedExerciseRowAdapter.VH>(
        object : DiffUtil.ItemCallback<LearningActivityItem>() {
            override fun areItemsTheSame(a: LearningActivityItem, b: LearningActivityItem) =
                a.id != null && a.id == b.id

            override fun areContentsTheSame(a: LearningActivityItem, b: LearningActivityItem) = a == b
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCompletedExerciseRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class VH(private val binding: ItemCompletedExerciseRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LearningActivityItem) {
            val ctx = binding.root.context
            binding.tvTitle.text = item.title?.takeIf { it.isNotBlank() }
                ?: item.activityType?.replace('_', ' ') ?: "—"

            val mins = (item.durationSeconds ?: 0) / 60
            val dateStr = formatCompletedDate(item.startedAt)
            binding.tvMeta.text = ctx.getString(R.string.completed_exercise_meta_format, mins, dateStr)

            val score = item.scorePercent?.toInt()
            if (score != null) {
                binding.tvScore.text = ctx.getString(R.string.profile_percent_format, score)
                binding.progressScore.progress = score.coerceIn(0, 100)
                binding.progressScore.visibility = android.view.View.VISIBLE
                val high = score >= 80
                binding.frameIcon.setBackgroundResource(
                    if (high) R.drawable.bg_completed_exercise_icon_green
                    else R.drawable.bg_completed_exercise_icon_blue
                )
            } else {
                binding.tvScore.text = "—"
                binding.progressScore.progress = 0
                binding.progressScore.visibility = android.view.View.GONE
                binding.frameIcon.setBackgroundResource(R.drawable.bg_completed_exercise_icon_blue)
            }

            val barColor = ContextCompat.getColor(
                ctx,
                if ((score ?: 0) >= 80) R.color.success else R.color.primary
            )
            binding.progressScore.progressTintList = ColorStateList.valueOf(barColor)
        }

        private fun formatCompletedDate(iso: String?): String {
            if (iso.isNullOrBlank()) return "—"
            val normalized = iso.take(19) // LocalDateTime part if Z suffix
            return try {
                val dt = LocalDateTime.parse(normalized)
                dt.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US))
            } catch (_: DateTimeParseException) {
                try {
                    val dt = LocalDateTime.parse(iso.substringBefore('[').trim())
                    dt.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US))
                } catch (_: Exception) {
                    iso
                }
            }
        }
    }
}
