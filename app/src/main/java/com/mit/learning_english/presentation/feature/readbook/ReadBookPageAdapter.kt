package com.mit.learning_english.presentation.feature.readbook

import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemReadBookPageBinding
import com.mit.learning_english.domain.model.Page

class ReadBookPageAdapter(
    private val onLookupTextSelected: (String) -> Unit
) :
    PagingDataAdapter<Page, ReadBookPageAdapter.PageViewHolder>(PageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = ItemReadBookPageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PageViewHolder(binding, onLookupTextSelected)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PageViewHolder(
        private val binding: ItemReadBookPageBinding,
        private val onLookupTextSelected: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            private const val MENU_LOOKUP_ID = 1001
        }

        fun bind(page: Page?) {
            if (page != null) {
                val content = "${page.number} ${page.sentences.joinToString(" ") { it.content }}"
                binding.tvPageContent.text = content
                setupSelectionLookup()
            } else {
                binding.tvPageContent.text = ""
            }
        }

        private fun setupSelectionLookup() {
            binding.tvPageContent.customSelectionActionModeCallback = object : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        val selectedText = binding.tvPageContent.text
            .substring(
                binding.tvPageContent.selectionStart,
                binding.tvPageContent.selectionEnd
            )
        if (selectedText.isNotBlank()) {
            onLookupTextSelected(selectedText) // Hiển thị dialog ngay
        }
        // Trả về false để không hiển thị menu
        return false
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean = false

    override fun onDestroyActionMode(mode: ActionMode?) = Unit
}
        }

        private fun getSelectedText(): String {
            val start = binding.tvPageContent.selectionStart
            val end = binding.tvPageContent.selectionEnd
            if (start < 0 || end < 0 || start == end) return ""
            val min = minOf(start, end)
            val max = maxOf(start, end)
            return binding.tvPageContent.text?.substring(min, max).orEmpty()
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
