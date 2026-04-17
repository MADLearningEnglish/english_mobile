package com.mit.learning_english.presentation.feature.readbook.lookup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemChooseDeckBinding
import com.mit.learning_english.domain.model.Deck

class DeckPickerAdapter(
    private val onDeckClick: (Deck) -> Unit
) : ListAdapter<Deck, DeckPickerAdapter.DeckViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val binding = ItemChooseDeckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DeckViewHolder(
        private val binding: ItemChooseDeckBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Deck) {
            binding.tvTitle.text = item.title
            binding.tvCount.text =
                binding.root.context.getString(R.string.choose_deck_card_count, item.flashcards.size)
            binding.root.setOnClickListener { onDeckClick(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Deck>() {
        override fun areItemsTheSame(oldItem: Deck, newItem: Deck): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Deck, newItem: Deck): Boolean = oldItem == newItem
    }
}
