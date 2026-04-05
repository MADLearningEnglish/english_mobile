package com.mit.learning_english.presentation.feature.profile.vocabulary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.mit.learning_english.databinding.ItemProfileVocabWordBinding
import com.mit.learning_english.domain.model.profile.VocabularyWord

class ProfileVocabularyAdapter(
    private val onFavorite: (VocabularyWord) -> Unit
) : PagingDataAdapter<VocabularyWord, ProfileVocabularyAdapter.VH>(
    object : DiffUtil.ItemCallback<VocabularyWord>() {
        override fun areItemsTheSame(a: VocabularyWord, b: VocabularyWord) = a.id == b.id
        override fun areContentsTheSame(a: VocabularyWord, b: VocabularyWord) = a == b
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemProfileVocabWordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding, onFavorite)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class VH(
        private val binding: ItemProfileVocabWordBinding,
        private val onFavorite: (VocabularyWord) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(w: VocabularyWord) {
            binding.tvTerm.text = w.term
            binding.tvPhonetic.text = w.phonetic.orEmpty()
            binding.tvDefinition.text = w.definition.orEmpty()
            binding.tvPhonetic.visibility =
                if (w.phonetic.isNullOrBlank()) android.view.View.GONE else android.view.View.VISIBLE
            binding.btnFavorite.setImageResource(
                if (w.favorite) com.mit.learning_english.R.drawable.ic_star_filled_voc
                else com.mit.learning_english.R.drawable.ic_star_outline_voc
            )
            binding.btnFavorite.setOnClickListener { onFavorite(w) }
            binding.btnSpeak.visibility = android.view.View.GONE
        }
    }
}
