package com.mit.learning_english.presentation.feature.bookbygenre

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemBookRecommendAllBinding
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.presentation.extensions.loadImage

class BookByGenreAdapter(
    private val onItemClick: (Book) -> Unit
) : PagingDataAdapter<Book, BookByGenreAdapter.BookByGenreViewHolder>(BookByGenreComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookByGenreViewHolder {
        val binding = ItemBookRecommendAllBinding.inflate(
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
        private val binding: ItemBookRecommendAllBinding,
        private val onItemClick: (Book) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Book) {
            binding.apply {
                tvTitleBook.text = item.title
                tvBookAuthor.text = item.authorsName
                tvBookDescription.text = item.blurb
                tvBookDuration.text = item.language
                ivBookCover.loadImage(item.coverUrl)
                root.setOnClickListener { onItemClick(item) }
            }
        }
    }

    private object BookByGenreComparator : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean =
            oldItem == newItem
    }
}
