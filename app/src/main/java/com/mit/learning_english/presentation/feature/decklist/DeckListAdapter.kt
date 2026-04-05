package com.mit.learning_english.presentation.feature.decklist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemDeckBinding
import com.mit.learning_english.domain.model.Deck

class DeckListAdapter(
    private val onStartClick: (Deck) -> Unit,
    private val onEditClick: (Deck) -> Unit,
    private val onDeleteClick: (Deck) -> Unit
) : ListAdapter<Deck, DeckListAdapter.DeckViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Deck>() {
            override fun areItemsTheSame(oldItem: Deck, newItem: Deck) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Deck, newItem: Deck) = oldItem == newItem
        }
    }

    inner class DeckViewHolder(
        private val binding: ItemDeckBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(deck: Deck) {
            binding.tvTitle.text = deck.title
            val wordCountLabel = if (deck.totalWords == 1) "1 từ" else "${deck.totalWords} từ"
            binding.tvWordCount.text = wordCountLabel

            // Cover image
            if (!deck.coverImageUrl.isNullOrBlank()) {
                Glide.with(binding.imgCover)
                    .load(deck.coverImageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_hero_placeholder)
                    .error(R.drawable.ic_hero_placeholder)
                    .into(binding.imgCover)
            } else {
                binding.imgCover.setImageResource(R.drawable.ic_hero_placeholder)
            }

            // Listeners
            binding.btnStart.setOnClickListener { onStartClick(deck) }
            binding.cardRoot.setOnClickListener { onStartClick(deck) }

            // Options Menu (Edit/Delete)
            binding.btnEdit.setOnClickListener { onEditClick(deck) }
            binding.btnDelete.setOnClickListener { onDeleteClick(deck) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val binding = ItemDeckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
