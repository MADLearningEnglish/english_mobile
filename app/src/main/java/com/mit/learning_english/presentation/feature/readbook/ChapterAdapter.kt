package com.mit.learning_english.presentation.feature.readbook

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemChapterReadBookBinding
import com.mit.learning_english.domain.model.Chapter
import java.util.Locale

/**
 * Adapter hiển thị danh sách chương trong ngăn điều hướng của màn hình đọc sách.
 */
class ChapterAdapter(
    private val onChapterClick: (Chapter) -> Unit
) : ListAdapter<Chapter, ChapterAdapter.ChapterViewHolder>(ChapterDiffCallback()) {

 private var activeChapterId: Int = -1

    /**
     * Lấy ID chương đang được đánh dấu hoạt động.
     */
    fun getActiveChapterId():Int{
        return activeChapterId
    }

    /**
     * Cập nhật chương đang active và refresh đúng các item cần thiết.
     */
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


    /**
     * Tạo ViewHolder cho item chương.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val binding = ItemChapterReadBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChapterViewHolder(binding)
    }

    /**
     * Bind dữ liệu chương và trạng thái active vào ViewHolder.
     */
    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = getItem(position)
        holder.bind(chapter, chapter.id == activeChapterId)
    }

    /**
     * ViewHolder hiển thị thông tin một chương và style theo trạng thái active.
     */
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

        /**
         * Gán dữ liệu chương và đổi màu item nếu đây là chương đang đọc.
         */
        fun bind(chapter: Chapter, isActive: Boolean) {
            binding.apply{
            tvNumber.text = String.format(Locale.getDefault(), "%02d", chapter.number)
            tvChapterTitle.text = chapter.title
            tvTotalPages.text = root.context.getString(R.string.total_page_format, chapter.totalPages)
            tvDurations.text = root.context.getString(R.string.hh_mm_format,chapter.totalDuration/3600,(chapter.totalDuration%3600)/60)

            val context = root.context
            if (isActive) {
                // Set background to @color/primary
                val primaryColor = ContextCompat.getColor(context, R.color.primary)
                root.backgroundTintList = ColorStateList.valueOf(primaryColor)

                tvNumber.setTextColor(Color.WHITE)
                tvChapterTitle.setTextColor(Color.WHITE)
                tvTotalPages.setTextColor(Color.WHITE)
                tvDurations.setTextColor(Color.WHITE)
                icDot.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            } else {
                // Revert to original colors based on item_chapter_read_book.xml
                val surfaceGrayColor = ContextCompat.getColor(context, R.color.surface_gray)
                val primaryColor = ContextCompat.getColor(context, R.color.primary)
                val bodyPrimaryColor = ContextCompat.getColor(context, R.color.body_primary)
                val bodyGrayColor = ContextCompat.getColor(context, R.color.body_gray)

                root.backgroundTintList = ColorStateList.valueOf(surfaceGrayColor)
                tvNumber.setTextColor(primaryColor)
                tvChapterTitle.setTextColor(bodyPrimaryColor)
                tvTotalPages.setTextColor(bodyGrayColor)
                tvDurations.setTextColor(bodyGrayColor)
                icDot.backgroundTintList = ColorStateList.valueOf(bodyGrayColor)
            }}
        }
    }

    /**
     * DiffUtil tối ưu cập nhật danh sách chương.
     */
    class ChapterDiffCallback : DiffUtil.ItemCallback<Chapter>() {
        override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem == newItem
        }
    }
}
