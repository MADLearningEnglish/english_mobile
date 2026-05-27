package com.mit.learning_english.presentation.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemBookRecommendBinding
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.presentation.extensions.loadImage

/**
 * Adapter hỗ trợ phân trang (Paging) hiển thị danh sách sách yêu thích của người dùng ở màn hình chính.
 */
class FavoriteBookPagingAdapter(
    private val onBookClick: (Book) -> Unit
) : PagingDataAdapter<Book, FavoriteBookPagingAdapter.FavoriteBookViewHolder>(BookComparator) {

    /**
     * Tạo ViewHolder mới bằng cách inflate layout ItemBookRecommendBinding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteBookViewHolder {
        val binding = ItemBookRecommendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FavoriteBookViewHolder(binding)
    }

    /**
     * Gán dữ liệu sách yêu thích ở vị trí tương ứng vào ViewHolder.
     */
    override fun onBindViewHolder(holder: FavoriteBookViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    /**
     * ViewHolder hiển thị chi tiết thông tin sách yêu thích.
     */
    inner class FavoriteBookViewHolder(
        private val binding: ItemBookRecommendBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Liên kết thông tin tiêu đề, tác giả, hình ảnh bìa và gán sự kiện click chọn sách yêu thích.
         */
        fun bind(book: Book) {
            binding.tvBookTitle.text = book.title
            binding.tvBookAuthor.text = book.authorsName
            binding.ivBookCover.loadImage(book.coverUrl)
            binding.root.setOnClickListener { onBookClick(book) }
        }
    }

    /**
     * Callback so sánh các cuốn sách yêu thích nhằm tối ưu hóa việc phân trang và hiển thị RecyclerView.
     */
    object BookComparator : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}
