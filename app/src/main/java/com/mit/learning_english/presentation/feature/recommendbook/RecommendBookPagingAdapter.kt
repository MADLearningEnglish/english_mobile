package com.mit.learning_english.presentation.feature.recommendbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemRecommendBookVerticalBinding
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.presentation.extensions.loadImage
import kotlin.math.ceil
import kotlin.math.max

/**
 * Adapter hỗ trợ phân trang (Paging) hiển thị danh sách sách đề xuất theo chiều dọc với chi tiết thông tin hơn.
 */
class RecommendBookPagingAdapter(
    private val onItemClick: (Book) -> Unit
) : PagingDataAdapter<Book, RecommendBookPagingAdapter.RecommendBookViewHolder>(BookComparator) {

    /**
     * Tạo ViewHolder mới bằng cách inflate layout ItemRecommendBookVerticalBinding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendBookViewHolder {
        val binding = ItemRecommendBookVerticalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecommendBookViewHolder(binding)
    }

    /**
     * Gán dữ liệu sách đề xuất tại vị trí tương ứng vào ViewHolder.
     */
    override fun onBindViewHolder(holder: RecommendBookViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    /**
     * ViewHolder đại diện cho giao diện một cuốn sách đề xuất theo chiều dọc.
     */
    inner class RecommendBookViewHolder(
        private val binding: ItemRecommendBookVerticalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Liên kết thông tin ảnh bìa, tiêu đề, tác giả, thể loại, mô tả,
         * đồng thời ước tính thời gian đọc dựa trên độ dài audio và số trang, sau đó gán sự kiện click.
         */
        fun bind(item: Book) {
            val readMinutes:Int = (item.chapters.sumOf { it.totalDuration }/3600)
            val pages = item.chapters.sumOf { chapter -> chapter.totalPages }
            val context = binding.root.context

            binding.ivBookCover.loadImage(item.coverUrl)
            binding.tvBookTitle.text = item.title
            binding.tvBookAuthor.text = item.authorsName
            binding.tvBookDescription.text = item.blurb
            binding.tvReadTimeAndPage.text = context.getString(R.string.recommend_read_time_format, readMinutes,pages)
            binding.tvGenre.text = item.genresName

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    /**
     * Callback so sánh các cuốn sách đề xuất để tối ưu hóa việc cập nhật danh sách RecyclerView.
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
