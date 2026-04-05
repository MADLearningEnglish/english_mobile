package com.mit.learning_english.presentation.feature.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemProfileActivityRowBinding
import com.mit.learning_english.domain.model.profile.LearningActivityItem

class ProfileActivityRowAdapter : ListAdapter<LearningActivityItem, ProfileActivityRowAdapter.VH>(
    object : DiffUtil.ItemCallback<LearningActivityItem>() {
        override fun areItemsTheSame(a: LearningActivityItem, b: LearningActivityItem) =
            a.id != null && a.id == b.id

        override fun areContentsTheSame(a: LearningActivityItem, b: LearningActivityItem) = a == b
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemProfileActivityRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(private val binding: ItemProfileActivityRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LearningActivityItem) {
            val ctx = binding.root.context
            val type = item.activityType?.uppercase().orEmpty()
            val title = item.title ?: type
            binding.tvTitle.text = title
            val mins = (item.durationSeconds ?: 0) / 60
            val extra = buildString {
                when {
                    type.contains("FLASHCARD") ->
                        append(" • ").append(item.wordsNewCount ?: 0).append(" words")
                    type.contains("LESSON") || type.contains("EXERCISE") ->
                        item.scorePercent?.let { append(" • ").append(it.toInt()).append("% score") }
                    type.contains("AI_CHAT") || type.contains("AI") -> { }
                }
            }
            binding.tvSubtitle.text = ctx.getString(R.string.profile_activity_subtitle_format, mins, extra)

            when {
                type.contains("LESSON") || type.contains("EXERCISE") -> {
                    binding.imgTypeIcon.setBackgroundResource(R.drawable.bg_profile_activity_icon_orange)
                    binding.imgTypeIcon.setImageResource(R.drawable.ic_activity_lesson)
                }
                type.contains("AI") -> {
                    binding.imgTypeIcon.setBackgroundResource(R.drawable.bg_profile_activity_icon_purple)
                    binding.imgTypeIcon.setImageResource(R.drawable.ic_activity_ai)
                }
                else -> {
                    binding.imgTypeIcon.setBackgroundResource(R.drawable.bg_profile_activity_icon_blue)
                    binding.imgTypeIcon.setImageResource(R.drawable.ic_activity_flashcard)
                }
            }
        }
    }
}
