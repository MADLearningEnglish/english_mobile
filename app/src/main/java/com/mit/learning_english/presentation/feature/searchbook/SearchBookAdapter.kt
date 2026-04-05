package com.mit.learning_english.presentation.feature.searchbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemSearchBookBinding
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.presentation.extensions.loadImage

class SearchBookAdapter(
    private val onItemClick: (Book) -> Unit
) : PagingDataAdapter<Book, SearchBookAdapter.SearchBookViewHolder>(SearchBookComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchBookViewHolder {
        val binding = ItemSearchBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchBookViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class SearchBookViewHolder(
        private val binding: ItemSearchBookBinding
    ) : RecyclerView.ViewHolder(binding.root) {
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

    object SearchBookComparator : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}
