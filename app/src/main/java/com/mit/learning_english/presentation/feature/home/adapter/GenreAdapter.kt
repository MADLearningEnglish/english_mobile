package com.mit.learning_english.presentation.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemGenreBinding
import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.presentation.extensions.loadImage

/**
 * Adapter hiển thị danh sách các thể loại sách nằm ngang trên màn hình chính.
 */
class GenreAdapter(
    private val onGenreClick: (Genre) -> Unit = { _ -> }
) : ListAdapter<Genre, GenreAdapter.GenresViewHolder>(GenreDiffCallback()) {
    
    /**
     * Tạo ViewHolder mới bằng cách inflate layout ItemGenreBinding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenresViewHolder {
        val binding = ItemGenreBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GenresViewHolder(binding, onGenreClick)
    }

    /**
     * Gán dữ liệu thể loại sách tại vị trí tương ứng vào ViewHolder.
     */
    override fun onBindViewHolder(holder: GenresViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    /**
     * ViewHolder đại diện cho giao diện một mục thể loại sách.
     */
    class GenresViewHolder(
        private val binding: ItemGenreBinding,
        private val onGenreClick: (Genre) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Liên kết thông tin tên thể loại, hình ảnh và thiết lập sự kiện click chọn thể loại.
         */
        fun bind(genre: Genre) {
            binding.tvGenreName.text = genre.name
            binding.imgGenre.loadImage(genre.thumbnail)
            binding.root.setOnClickListener { onGenreClick(genre) }
        }
    }

    /**
     * Callback DiffUtil so sánh thông tin các thể loại để tối ưu hóa việc cập nhật danh sách.
     */
    class GenreDiffCallback : DiffUtil.ItemCallback<Genre>() {
        override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem == newItem
        }
    }
}