package com.mit.learning_english.presentation.feature.historyreadbook

import android.util.Log
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

/**
 * Adapter phân trang hiển thị danh sách sách trong lịch sử đọc.
 */
class HistoryReadBookPagingAdapter(
    private val onItemClick: (BookReponse) -> Unit
) : PagingDataAdapter<BookReponse, HistoryReadBookPagingAdapter.HistoryBookViewHolder>(DiffCallback) {

    /**
     * Tạo ViewHolder cho từng item sách lịch sử.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryBookViewHolder {
        val binding = ItemHistoryBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryBookViewHolder(binding)
    }

    /**
     * Bind dữ liệu trang hiện tại vào ViewHolder.
     */
    override fun onBindViewHolder(holder: HistoryBookViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    /**
     * ViewHolder hiển thị thông tin một cuốn sách đã đọc.
     */
    inner class HistoryBookViewHolder(
        private val binding: ItemHistoryBookBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Gán dữ liệu hiển thị tiêu đề, tiến độ và thời gian đọc gần nhất.
         */
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
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    /**
     * DiffUtil tối ưu cập nhật item trong danh sách phân trang.
     */
    private object DiffCallback : DiffUtil.ItemCallback<BookReponse>() {
        override fun areItemsTheSame(oldItem: BookReponse, newItem: BookReponse): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: BookReponse, newItem: BookReponse): Boolean =
            oldItem == newItem
    }
}
