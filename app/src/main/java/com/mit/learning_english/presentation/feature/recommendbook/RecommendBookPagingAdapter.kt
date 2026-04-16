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

class RecommendBookPagingAdapter(
    private val onItemClick: (Book) -> Unit
) : PagingDataAdapter<Book, RecommendBookPagingAdapter.RecommendBookViewHolder>(BookComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendBookViewHolder {
        val binding = ItemRecommendBookVerticalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecommendBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendBookViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    inner class RecommendBookViewHolder(
        private val binding: ItemRecommendBookVerticalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

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

    object BookComparator : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}
