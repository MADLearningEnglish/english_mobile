package com.mit.learning_english.presentation.feature.searchbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemSearchBookBinding
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.presentation.extensions.loadImage

/**
 * Adapter hỗ trợ phân trang (Paging) hiển thị danh sách sách tìm kiếm được.
 */
class SearchBookAdapter(
    private val onItemClick: (Book) -> Unit
) : PagingDataAdapter<Book, SearchBookAdapter.SearchBookViewHolder>(SearchBookComparator) {

    /**
     * Tạo ViewHolder mới bằng cách inflate layout ItemSearchBookBinding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchBookViewHolder {
        val binding = ItemSearchBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchBookViewHolder(binding)
    }

    /**
     * Gán dữ liệu sách tìm kiếm được tại vị trí tương ứng vào ViewHolder.
     */
    override fun onBindViewHolder(holder: SearchBookViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    /**
     * ViewHolder đại diện cho giao diện một mục sách trong danh sách tìm kiếm.
     */
    inner class SearchBookViewHolder(
        private val binding: ItemSearchBookBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Liên kết thông tin tiêu đề, tác giả, ngôn ngữ (được hiển thị tại trường tvGenre), ảnh bìa và gán sự kiện click.
         */
        fun bind(item: Book) {
            binding.apply {
                tvBookTitle.text = item.title
                tvBookAuthor.text = item.authorsName
                tvGenre.text = item.language
                ivBookCover.loadImage(item.coverUrl)
                root.setOnClickListener { onItemClick(item) }
            }
        }
    }

    /**
     * Callback so sánh các cuốn sách kết quả tìm kiếm để tối ưu hóa việc vẽ lại RecyclerView.
     */
    object SearchBookComparator : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}
