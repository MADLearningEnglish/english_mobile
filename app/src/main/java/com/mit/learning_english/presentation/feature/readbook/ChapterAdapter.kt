package com.mit.learning_english.presentation.feature.readbook

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemChapterReadBookBinding
import com.mit.learning_english.domain.model.Chapter

class ChapterAdapter(
    private val onChapterClick: (Chapter) -> Unit
) : ListAdapter<Chapter, ChapterAdapter.ChapterViewHolder>(ChapterDiffCallback()) {

    private var activeChapterId: Int = -1

    fun setActiveChapterId(chapterId: Int) {
        if (activeChapterId != chapterId) {
            val oldId = activeChapterId
            activeChapterId = chapterId
            
            // Notify changes to update UI
            val oldActiveIndex = currentList.indexOfFirst { it.id == oldId }
            val newActiveIndex = currentList.indexOfFirst { it.id == chapterId }
            
            if (oldActiveIndex != -1) notifyItemChanged(oldActiveIndex)
            if (newActiveIndex != -1) notifyItemChanged(newActiveIndex)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val binding = ItemChapterReadBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = getItem(position)
        holder.bind(chapter, chapter.id == activeChapterId)
    }

    inner class ChapterViewHolder(private val binding: ItemChapterReadBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onChapterClick(getItem(position))
                }
            }
        }

        fun bind(chapter: Chapter, isActive: Boolean) {
            binding.tvNumber.text = String.format("%02d", chapter.number)
            binding.tvChapterTitle.text = chapter.title
            binding.tvTotalPages.text = "${chapter.totalPages} pages"
            binding.tvDurations.text = String.format("%02d:%02d minus", chapter.totalDuration / 60, chapter.totalDuration % 60)

            val context = binding.root.context
            if (isActive) {
                // Set background to @color/primary
                val primaryColor = ContextCompat.getColor(context, R.color.primary)
                binding.root.backgroundTintList = ColorStateList.valueOf(primaryColor)
                
                binding.tvNumber.setTextColor(Color.WHITE)
                binding.tvChapterTitle.setTextColor(Color.WHITE)
                binding.tvTotalPages.setTextColor(Color.WHITE)
                binding.tvDurations.setTextColor(Color.WHITE)
                binding.icDot.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            } else {
                // Revert to original colors based on item_chapter_read_book.xml
                val surfaceGrayColor = ContextCompat.getColor(context, R.color.surface_gray)
                val primaryColor = ContextCompat.getColor(context, R.color.primary)
                val bodyPrimaryColor = ContextCompat.getColor(context, R.color.body_primary)
                val bodyGrayColor = ContextCompat.getColor(context, R.color.body_gray)
                
                binding.root.backgroundTintList = ColorStateList.valueOf(surfaceGrayColor)
                binding.tvNumber.setTextColor(primaryColor)
                binding.tvChapterTitle.setTextColor(bodyPrimaryColor)
                binding.tvTotalPages.setTextColor(bodyGrayColor)
                binding.tvDurations.setTextColor(bodyGrayColor)
                binding.icDot.backgroundTintList = ColorStateList.valueOf(bodyGrayColor)
            }
        }
    }

    class ChapterDiffCallback : DiffUtil.ItemCallback<Chapter>() {
        override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem == newItem
        }
    }
}
