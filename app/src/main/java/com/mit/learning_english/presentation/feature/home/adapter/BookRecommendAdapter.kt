package com.mit.learning_english.presentation.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemBookRecommendBinding
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.presentation.extensions.loadImage

/**
 * Adapter hiển thị danh sách các cuốn sách được đề xuất dưới dạng danh sách ngang ở màn hình chính.
 */
class BookRecommendAdapter(
    private val onBookClick: (Book) -> Unit
) : ListAdapter<Book, BookRecommendAdapter.BookRecommendViewHolder>(BookDiffCallback()) {
    
    /**
     * Tạo ViewHolder mới bằng cách inflate layout ItemBookRecommendBinding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookRecommendViewHolder {
        val binding = ItemBookRecommendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookRecommendViewHolder(binding)
    }

    /**
     * Gán dữ liệu sách đề xuất tại vị trí tương ứng vào ViewHolder và xử lý sự kiện click chọn sách.
     */
    override fun onBindViewHolder(holder: BookRecommendViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item) { onBookClick(item) }
    }

    /**
     * ViewHolder chứa giao diện hiển thị bìa sách, tiêu đề sách và tên tác giả.
     */
    class BookRecommendViewHolder(private val binding: ItemBookRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Liên kết thông tin tiêu đề, tác giả, hình ảnh bìa và gán sự kiện click vào item sách.
         */
        fun bind(book: Book, onBookClick: () -> Unit) {
            binding.root.setOnClickListener { onBookClick() }
            binding.tvBookTitle.text = book.title
            binding.tvBookAuthor.text = book.authorsName
            binding.ivBookCover.loadImage(book.coverUrl)
        }
    }

    /**
     * Callback DiffUtil so sánh thông tin các cuốn sách đề xuất để cập nhật danh sách hiệu quả.
     */
    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}