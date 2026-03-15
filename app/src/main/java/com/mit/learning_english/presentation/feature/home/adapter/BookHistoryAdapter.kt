package com.mit.learning_english.presentation.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemHistoryBookBinding
import com.mit.learning_english.domain.model.BookHistory
import com.mit.learning_english.presentation.extensions.loadImage

class BookHistoryAdapter :
    PagingDataAdapter<BookHistory, BookHistoryAdapter.BookHistoryViewHolder>(BookHistoryComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHistoryViewHolder {
        val binding = ItemHistoryBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookHistoryViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class BookHistoryViewHolder(private val binding: ItemHistoryBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BookHistory) {
            binding.apply {
                tvBookTitle.text = item.title
                ivBookCover.loadImage(item.coverUrl)
                tvBookAuthor.text = item.authorsName
                pbProgress.progress = (item.processPercent * 100).toInt()
            }
        }
    }

    object BookHistoryComparator : DiffUtil.ItemCallback<BookHistory>() {
        override fun areItemsTheSame(oldItem: BookHistory, newItem: BookHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BookHistory, newItem: BookHistory): Boolean {
            return oldItem == newItem
        }
    }
}
