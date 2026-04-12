package com.mit.learning_english.presentation.feature.decklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemDeckBinding
import com.mit.learning_english.domain.model.Deck

class DeckListAdapter(
    private val onStartClick: (Deck) -> Unit,
    private val onEditClick: (Deck) -> Unit, // Keeping these to avoid breaking Fragment method signatures
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
            binding.tvWordCount.text = "Học phần • ${deck.flashcards.size} thuật ngữ"

            // Allow touching the entire row to start reviewing
            binding.cardRoot.setOnClickListener { onStartClick(deck) }
            
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
