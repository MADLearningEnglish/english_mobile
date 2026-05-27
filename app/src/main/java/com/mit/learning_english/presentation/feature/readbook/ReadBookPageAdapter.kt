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

/**
 * Adapter phân trang hiển thị nội dung từng trang sách.
 */
class ReadBookPageAdapter(
    private val onLookupTextSelected: (String) -> Unit
) :
    PagingDataAdapter<Page, ReadBookPageAdapter.PageViewHolder>(PageDiffCallback()) {

    /**
     * Tạo ViewHolder cho item trang sách.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = ItemReadBookPageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PageViewHolder(binding, onLookupTextSelected)
    }

    /**
     * Bind dữ liệu trang vào ViewHolder theo vị trí hiện tại.
     */
    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder hiển thị nội dung một trang và xử lý tra từ khi bôi đen.
     */
    class PageViewHolder(
        private val binding: ItemReadBookPageBinding,
        private val onLookupTextSelected: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Gán nội dung văn bản và số trang cho UI.
         */
        fun bind(page: Page?) {
            if (page != null) {
                val content =  page.sentences.joinToString(" ") { it.content }
                binding.tvPageContent.text = content
                binding.tvPageNumber.text = page.number.toString()
                setupSelectionLookup()
            } else {
                binding.tvPageContent.text = ""
            }
        }

        /**
         * Chặn menu mặc định và gửi đoạn text đã chọn về callback tra từ.
         */
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
    }

    /**
     * DiffUtil hỗ trợ tối ưu cập nhật dữ liệu trang.
     */
    class PageDiffCallback : DiffUtil.ItemCallback<Page>() {
        override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem == newItem
        }
    }
}
