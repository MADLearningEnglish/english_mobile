package com.mit.learning_english.presentation.feature.recommendbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemHistoryBookBinding
import com.mit.learning_english.domain.model.BookReponse
import com.mit.learning_english.presentation.extensions.loadImage

class ReadingInProgressHorizontalAdapter(
    private val onBookClick: (BookReponse) -> Unit
) : ListAdapter<BookReponse, ReadingInProgressHorizontalAdapter.VH>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemHistoryBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val widthPx = parent.context.resources.getDimensionPixelSize(
            com.mit.learning_english.R.dimen.reading_in_progress_card_width
        )
        binding.root.layoutParams = RecyclerView.LayoutParams(
            widthPx,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), onBookClick)
    }

    class VH(private val binding: ItemHistoryBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BookReponse, onBookClick: (BookReponse) -> Unit) {
            binding.root.setOnClickListener { onBookClick(item) }
            binding.tvBookTitle.text = item.title
            binding.ivBookCover.loadImage(item.coverUrl)
            binding.tvBookAuthor.text = item.authorsName
            binding.pbProgress.progress = item.processPercent.toInt().coerceIn(0, 100)
        }
    }

    private object Diff : DiffUtil.ItemCallback<BookReponse>() {
        override fun areItemsTheSame(oldItem: BookReponse, newItem: BookReponse): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: BookReponse, newItem: BookReponse): Boolean =
            oldItem == newItem
    }
}
