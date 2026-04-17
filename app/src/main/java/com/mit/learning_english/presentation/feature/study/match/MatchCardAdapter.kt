package com.mit.learning_english.presentation.feature.study.match

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mit.learning_english.databinding.ItemMatchCardBinding
import com.mit.learning_english.shared.MediaUrlResolver

class MatchCardAdapter(
    private val onItemClick: (String) -> Unit
) : ListAdapter<MatchCard, MatchCardAdapter.ViewHolder>(MatchCardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemMatchCardBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val card = getItem(adapterPosition)
                    if (card.state != CardState.CORRECT && card.state != CardState.HIDDEN && card.state != CardState.INCORRECT) {
                        onItemClick(card.id)
                    }
                }
            }
        }

        fun bind(card: MatchCard) {
            binding.tvContent.text = card.text
            
            val imageUrl = MediaUrlResolver.resolve(card.imageUrl)
            if (!card.isTerm && !imageUrl.isNullOrBlank()) {
                binding.ivBackground.visibility = View.VISIBLE
                Glide.with(binding.root.context).load(imageUrl).into(binding.ivBackground)
            } else {
                binding.ivBackground.visibility = View.GONE
            }

            when (card.state) {
                CardState.UNSELECTED -> {
                    binding.rootLayout.setBackgroundColor(Color.WHITE)
                    binding.tvContent.setTextColor(Color.parseColor("#333333"))
                    binding.root.alpha = 1.0f
                    binding.root.visibility = View.VISIBLE
                }
                CardState.SELECTED -> {
                    binding.rootLayout.setBackgroundColor(Color.parseColor("#E1F5FE"))
                    binding.tvContent.setTextColor(Color.parseColor("#0288D1"))
                    binding.root.alpha = 1.0f
                    binding.root.visibility = View.VISIBLE
                }
                CardState.CORRECT -> {
                    binding.rootLayout.setBackgroundColor(Color.parseColor("#388E3C"))
                    binding.tvContent.setTextColor(Color.WHITE)
                    binding.root.alpha = 1.0f
                    binding.root.visibility = View.VISIBLE
                }
                CardState.INCORRECT -> {
                    binding.rootLayout.setBackgroundColor(Color.parseColor("#D32F2F"))
                    binding.tvContent.setTextColor(Color.WHITE)
                    binding.root.alpha = 1.0f
                    binding.root.visibility = View.VISIBLE
                }
                CardState.HIDDEN -> {
                    binding.root.alpha = 0.0f
                    binding.root.visibility = View.INVISIBLE
                }
            }
        }
    }
}

class MatchCardDiffCallback : DiffUtil.ItemCallback<MatchCard>() {
    override fun areItemsTheSame(oldItem: MatchCard, newItem: MatchCard) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: MatchCard, newItem: MatchCard) = oldItem == newItem
}
