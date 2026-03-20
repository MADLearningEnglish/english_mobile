package com.mit.learning_english.presentation.feature.createdeck

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemFlashcardInputCardBinding
import com.mit.learning_english.databinding.ItemFlashcardInputCollapsedBinding
import com.mit.learning_english.domain.model.FlashcardInput

private const val VIEW_TYPE_COLLAPSED = 0
private const val VIEW_TYPE_EXPANDED = 1

data class FlashcardUiItem(
    val input: FlashcardInput,
    val index: Int,
    val isExpanded: Boolean
)

class FlashcardInputAdapter(
    private val onToggleExpand: (Int) -> Unit,
    private val onDelete: (Int) -> Unit,
    private val onWordChanged: (Int, String) -> Unit,
    private val onPhoneticChanged: (Int, String) -> Unit,
    private val onMeaningChanged: (Int, String) -> Unit,
    private val onExampleChanged: (Int, String) -> Unit,
    private val onVisualCueClick: (Int) -> Unit
) : ListAdapter<FlashcardUiItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).isExpanded) VIEW_TYPE_EXPANDED else VIEW_TYPE_COLLAPSED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_EXPANDED) {
            ExpandedViewHolder(ItemFlashcardInputCardBinding.inflate(inflater, parent, false))
        } else {
            CollapsedViewHolder(ItemFlashcardInputCollapsedBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ExpandedViewHolder -> holder.bind(item)
            is CollapsedViewHolder -> holder.bind(item)
        }
    }

    inner class CollapsedViewHolder(
        private val binding: ItemFlashcardInputCollapsedBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FlashcardUiItem) {
            val displayWord = item.input.word.ifBlank { "Từ mới #${item.index + 1}" }
            val displayPhonetic = item.input.phonetic.ifBlank { "..." }
            binding.tvIndex.text = (item.index + 1).toString()
            binding.tvWord.text = displayWord
            binding.tvPhonetic.text = displayPhonetic
            binding.btnEdit.setOnClickListener { onToggleExpand(item.index) }
            binding.root.setOnClickListener { onToggleExpand(item.index) }
        }
    }

    inner class ExpandedViewHolder(
        private val binding: ItemFlashcardInputCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentIndex = -1
        private val wordWatcher = simpleWatcher { onWordChanged(currentIndex, it) }
        private val phoneticWatcher = simpleWatcher { onPhoneticChanged(currentIndex, it) }
        private val meaningWatcher = simpleWatcher { onMeaningChanged(currentIndex, it) }
        private val exampleWatcher = simpleWatcher { onExampleChanged(currentIndex, it) }

        init {
            binding.btnDeleteCard.setOnClickListener { if (currentIndex >= 0) onDelete(currentIndex) }
            binding.btnUploadVisualCue.setOnClickListener { if (currentIndex >= 0) onVisualCueClick(currentIndex) }
        }

        fun bind(item: FlashcardUiItem) {
            currentIndex = item.index
            binding.tvCardIndex.text = "THẺ #${item.index + 1}"

            // Remove watchers before setting text to avoid firing callbacks
            binding.etWord.removeTextChangedListener(wordWatcher)
            binding.etPhonetic.removeTextChangedListener(phoneticWatcher)
            binding.etMeaning.removeTextChangedListener(meaningWatcher)
            binding.etExample.removeTextChangedListener(exampleWatcher)

            binding.etWord.setText(item.input.word)
            binding.etPhonetic.setText(item.input.phonetic)
            binding.etMeaning.setText(item.input.meaning)
            binding.etExample.setText(item.input.exampleSentence)

            // Show visual cue
            val visualUri = item.input.visualCueUri
            val visualUrl = item.input.visualCueUrl
            if (visualUri != null) {
                binding.imgVisualCuePreview.visibility = android.view.View.VISIBLE
                binding.imgVisualCuePreview.setImageURI(visualUri)
                binding.layoutUploadPrompt.visibility = android.view.View.GONE
            } else if (!visualUrl.isNullOrEmpty()) {
                binding.imgVisualCuePreview.visibility = android.view.View.VISIBLE
                com.bumptech.glide.Glide.with(binding.root)
                    .load(visualUrl)
                    .centerCrop()
                    .into(binding.imgVisualCuePreview)
                binding.layoutUploadPrompt.visibility = android.view.View.GONE
            } else {
                binding.imgVisualCuePreview.visibility = android.view.View.GONE
                binding.imgVisualCuePreview.setImageDrawable(null)
                binding.layoutUploadPrompt.visibility = android.view.View.VISIBLE
            }

            // Move cursors to end
            binding.etWord.setSelection(binding.etWord.text?.length ?: 0)
            binding.etMeaning.setSelection(binding.etMeaning.text?.length ?: 0)

            // Re-attach watchers
            binding.etWord.addTextChangedListener(wordWatcher)
            binding.etPhonetic.addTextChangedListener(phoneticWatcher)
            binding.etMeaning.addTextChangedListener(meaningWatcher)
            binding.etExample.addTextChangedListener(exampleWatcher)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FlashcardUiItem>() {
            override fun areItemsTheSame(old: FlashcardUiItem, new: FlashcardUiItem) =
                old.index == new.index
            override fun areContentsTheSame(old: FlashcardUiItem, new: FlashcardUiItem) =
                old == new
        }
    }
}

private fun simpleWatcher(action: (String) -> Unit) = object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    override fun afterTextChanged(s: Editable?) { action(s?.toString() ?: "") }
}
