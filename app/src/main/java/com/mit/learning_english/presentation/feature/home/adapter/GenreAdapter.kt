package com.mit.learning_english.presentation.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemGenreBinding
import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.presentation.extensions.loadImage

class GenreAdapter : ListAdapter<Genre, GenreAdapter.GenresViewHolder>(GenreDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenresViewHolder {
        val binding = ItemGenreBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GenresViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenresViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class GenresViewHolder(
        private val binding: ItemGenreBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(genre: Genre) {
            binding.tvGenreName.text = genre.name
            binding.imgGenre.loadImage(genre.thumbnail)
        }
    }

    class GenreDiffCallback : DiffUtil.ItemCallback<Genre>() {
        override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem == newItem
        }
    }
}