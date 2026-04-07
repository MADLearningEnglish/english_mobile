package com.mit.learning_english.presentation.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemBookRecommendBinding
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.presentation.extensions.loadImage

class FavoriteBookPagingAdapter(
    private val onBookClick: (Book) -> Unit
) : PagingDataAdapter<Book, FavoriteBookPagingAdapter.FavoriteBookViewHolder>(BookComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteBookViewHolder {
        val binding = ItemBookRecommendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FavoriteBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteBookViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    inner class FavoriteBookViewHolder(
        private val binding: ItemBookRecommendBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.tvBookTitle.text = book.title
            binding.tvBookAuthor.text = book.authorsName
            binding.ivBookCover.loadImage(book.coverUrl)
            binding.root.setOnClickListener { onBookClick(book) }
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
