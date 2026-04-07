package com.mit.learning_english.presentation.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemAuthorBinding
import com.mit.learning_english.domain.model.Author
import com.mit.learning_english.presentation.extensions.loadAvatar

class AuthorAdapter(
    private val onItemClick: (Author) -> Unit
) : PagingDataAdapter<Author, AuthorAdapter.AuthorViewHolder>(AuthorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorViewHolder {
        val binding = ItemAuthorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AuthorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AuthorViewHolder, position: Int) {
        getItem(position)?.let { author ->
            holder.bind(author, onItemClick)
        }
    }

    class AuthorViewHolder(
        private val binding: ItemAuthorBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(author: Author, onItemClick: (Author) -> Unit) {
            binding.ivAuthorAvatar.loadAvatar(author.avatar, binding.shimmerAvatar)
            binding.tvAuthorName.text = author.name
            binding.tvAuthorBiography.text = author.biography
            binding.root.setOnClickListener { onItemClick(author) }
        }
    }

    class AuthorDiffCallback : DiffUtil.ItemCallback<Author>() {
        override fun areItemsTheSame(oldItem: Author, newItem: Author): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Author, newItem: Author): Boolean {
            return oldItem == newItem
        }
    }
}
