package com.mit.learning_english.presentation.feature.onboardingafterlogin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemOnboardingGenreGridBinding
import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.presentation.extensions.loadImage
import kotlin.math.roundToInt

class OnboardingGenreGridAdapter(
    private val onGenreToggle: (Int) -> Unit
) : ListAdapter<Genre, OnboardingGenreGridAdapter.VH>(Diff) {

    private var selectedIds: Set<Int> = emptySet()

    fun submitGenres(list: List<Genre>, selected: Set<Int>) {
        val selectionChanged = selectedIds != selected
        selectedIds = selected
        when {
            currentList != list -> submitList(list)
            selectionChanged -> notifyDataSetChanged()
            else -> Unit
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemOnboardingGenreGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onGenreToggle) { selectedIds }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemOnboardingGenreGridBinding,
        private val onToggle: (Int) -> Unit,
        private val selectedIds: () -> Set<Int>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(genre: Genre) {
            binding.tvGenreName.text = genre.name
            binding.imgGenre.loadImage(genre.thumbnail)
            val selected = genre.id in selectedIds()
            val density = binding.root.resources.displayMetrics.density
            binding.root.strokeWidth = if (selected) (2f * density).roundToInt().coerceAtLeast(1) else 0
            binding.root.strokeColor = ContextCompat.getColor(
                binding.root.context,
                R.color.primary
            )
            binding.root.setOnClickListener { onToggle(genre.id) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Genre>() {
        override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean =
            oldItem == newItem
    }
}
