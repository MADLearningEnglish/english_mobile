package com.mit.learning_english.presentation.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemBookRecommendBinding
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.presentation.extensions.loadImage

class BookRecommendAdapter :
    ListAdapter<Book, BookRecommendAdapter.BookRecommendViewHolder>(BookDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookRecommendViewHolder {
        val binding = ItemBookRecommendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookRecommendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookRecommendViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class BookRecommendViewHolder(private val binding: ItemBookRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.tvBookTitle.text = book.title
            binding.tvBookAuthor.text = book.authorsName
            binding.ivBookCover.loadImage(book.coverUrl)
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}