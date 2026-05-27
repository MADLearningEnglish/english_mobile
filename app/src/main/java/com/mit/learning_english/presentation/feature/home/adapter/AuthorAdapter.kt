package com.mit.learning_english.presentation.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemAuthorBinding
import com.mit.learning_english.domain.model.Author
import com.mit.learning_english.presentation.extensions.loadAvatar

/**
 * Adapter hỗ trợ phân trang (Paging) hiển thị danh sách các tác giả ở màn hình chính.
 */
class AuthorAdapter(
    private val onItemClick: (Author) -> Unit
) : PagingDataAdapter<Author, AuthorAdapter.AuthorViewHolder>(AuthorDiffCallback()) {

    /**
     * Tạo ViewHolder mới bằng cách inflate layout ItemAuthorBinding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorViewHolder {
        val binding = ItemAuthorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AuthorViewHolder(binding)
    }

    /**
     * Gán dữ liệu của tác giả ở vị trí tương ứng vào ViewHolder nếu dữ liệu không null.
     */
    override fun onBindViewHolder(holder: AuthorViewHolder, position: Int) {
        getItem(position)?.let { author ->
            holder.bind(author, onItemClick)
        }
    }

    /**
     * ViewHolder hiển thị thông tin chi tiết một tác giả.
     */
    class AuthorViewHolder(
        private val binding: ItemAuthorBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Liên kết thông tin avatar, tên tác giả, tiểu sử và đăng ký sự kiện click chọn tác giả.
         */
        fun bind(author: Author, onItemClick: (Author) -> Unit) {
            binding.ivAuthorAvatar.loadAvatar(author.avatar, binding.shimmerAvatar)
            binding.tvAuthorName.text = author.name
            binding.tvAuthorBiography.text = author.biography
            binding.root.setOnClickListener { onItemClick(author) }
        }
    }

    /**
     * Callback DiffUtil so sánh thông tin các tác giả để cập nhật RecyclerView hiệu quả hơn.
     */
    class AuthorDiffCallback : DiffUtil.ItemCallback<Author>() {
        override fun areItemsTheSame(oldItem: Author, newItem: Author): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Author, newItem: Author): Boolean {
            return oldItem == newItem
        }
    }
}
