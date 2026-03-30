package com.mit.learning_english.presentation.feature.readbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemReadBookPageBinding
import com.mit.learning_english.domain.model.Page
import java.io.File.separator

class ReadBookPageAdapter :
    ListAdapter<Page, ReadBookPageAdapter.PageViewHolder>(PageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = ItemReadBookPageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PageViewHolder(
        private val binding: ItemReadBookPageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(page: Page) {
            binding.tvPageContent.text = page.number.toString()+" - "+ page.sentences.joinToString(separator = " ") { it.content }
        }
    }

    class PageDiffCallback : DiffUtil.ItemCallback<Page>() {
        override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem == newItem
        }
    }
}
