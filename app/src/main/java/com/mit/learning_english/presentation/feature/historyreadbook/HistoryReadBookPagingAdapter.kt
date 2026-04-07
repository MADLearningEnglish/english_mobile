package com.mit.learning_english.presentation.feature.historyreadbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemHistoryBookBinding
import com.mit.learning_english.domain.model.BookReponse
import com.mit.learning_english.presentation.extensions.loadImage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoryReadBookPagingAdapter(
    private val onItemClick: (BookReponse) -> Unit
) : PagingDataAdapter<BookReponse, HistoryReadBookPagingAdapter.HistoryBookViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryBookViewHolder {
        val binding = ItemHistoryBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryBookViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    inner class HistoryBookViewHolder(
        private val binding: ItemHistoryBookBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BookReponse) {
            val context = binding.root.context
            val progressValue = item.processPercent.toInt().coerceIn(0, 100)
            val lastReadText = if (item.lastRead == LocalDateTime.MIN) {
                context.getString(R.string.history_last_read_time_unknown)
            } else {
                context.getString(
                    R.string.history_last_read_time_format,
                    item.lastRead.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault()))
                )
            }

            binding.ivBookCover.loadImage(item.coverUrl)
            binding.tvBookTitle.text = item.title
            binding.tvBookAuthor.text = item.authorsName
            binding.pbProgress.progress = progressValue
            binding.tvProgressPercent.text = context.getString(R.string.profile_percent_format, progressValue)
            binding.tvLastReadTime.text = lastReadText
            binding.tvLastReadPage.text = context.getString(
                R.string.history_last_read_page_format,
                item.pageLastRead
            )
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<BookReponse>() {
        override fun areItemsTheSame(oldItem: BookReponse, newItem: BookReponse): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: BookReponse, newItem: BookReponse): Boolean =
            oldItem == newItem
    }
}
