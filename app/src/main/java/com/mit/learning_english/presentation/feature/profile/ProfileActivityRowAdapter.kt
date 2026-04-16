package com.mit.learning_english.presentation.feature.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemProfileActivityRowBinding
import com.mit.learning_english.domain.model.profile.LearningActivityItem

class ProfileActivityRowAdapter(
    var onItemClick: ((LearningActivityItem) -> Unit)? = null
) : ListAdapter<LearningActivityItem, ProfileActivityRowAdapter.VH>(
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
        holder.bind(getItem(position), onItemClick)
    }

    class VH(private val binding: ItemProfileActivityRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LearningActivityItem, onItemClick: ((LearningActivityItem) -> Unit)?) {
            val ctx = binding.root.context
            val type = item.activityType?.uppercase().orEmpty()
            val rawTitle = item.title?.trim().orEmpty()
            binding.tvTitle.text = when {
                type.contains("BOOK") && rawTitle.isNotEmpty() ->
                    ctx.getString(R.string.profile_activity_title_book_format, rawTitle)
                type.contains("BOOK") ->
                    ctx.getString(R.string.book)
                type.contains("EXERCISE") && rawTitle.isNotEmpty() ->
                    ctx.getString(R.string.profile_activity_title_exercise_format, rawTitle)
                type.contains("EXERCISE") ->
                    ctx.getString(R.string.profile_activity_exercise_label)
                else -> item.title?.ifBlank { null } ?: type.replace('_', ' ')
            }
            val mins = (item.durationSeconds ?: 0) / 60
            val extra = buildString {
                when {
                    type.contains("FLASHCARD") ->
                        append(" • ").append(item.wordsNewCount ?: 0).append(" words")
                    type.contains("BOOK") || type.contains("EXERCISE") ->
                        item.scorePercent?.let { append(" • ").append(it.toInt()).append("% score") }
                    type.contains("AI_CHAT") || type.contains("AI") -> { }
                }
            }
            binding.tvSubtitle.text = ctx.getString(R.string.profile_activity_subtitle_format, mins, extra)

            when {
                type.contains("BOOK") -> {
                    binding.imgTypeIcon.setBackgroundResource(R.drawable.bg_profile_activity_icon_orange)
                    binding.imgTypeIcon.setImageResource(R.drawable.ic_book)
                }
                type.contains("EXERCISE") -> {
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
            binding.root.isClickable = onItemClick != null
            binding.root.isFocusable = onItemClick != null
            binding.root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }
}
