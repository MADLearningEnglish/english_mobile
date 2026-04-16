package com.mit.learning_english.presentation.feature.booksbygenre

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemRecommendBookVerticalBinding
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.presentation.extensions.loadImage

class BookByGenreAdapter(
    private val onItemClick: (Book) -> Unit
) : PagingDataAdapter<Book, BookByGenreAdapter.BookByGenreViewHolder>(BookByGenreComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookByGenreViewHolder {
        val binding = ItemRecommendBookVerticalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookByGenreViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: BookByGenreViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class BookByGenreViewHolder(
        private val binding: ItemRecommendBookVerticalBinding,
        private val onItemClick: (Book) -> Unit
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

    private object BookByGenreComparator : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean =
            oldItem == newItem
    }
}
