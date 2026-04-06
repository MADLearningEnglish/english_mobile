package com.mit.learning_english.presentation.feature.editdeck

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemFlashcardInputCardBinding
import com.mit.learning_english.databinding.ItemFlashcardInputCollapsedBinding
import com.mit.learning_english.domain.model.FlashcardUpdateInput

private const val VIEW_TYPE_COLLAPSED = 0
private const val VIEW_TYPE_EXPANDED = 1

data class FlashcardEditUiItem(
    val card: FlashcardUpdateInput,
    val originalIndex: Int,
    val isExpanded: Boolean
)

class FlashcardEditAdapter(
    private val onToggleExpand: (Int) -> Unit,
    private val onDelete: (Int) -> Unit,
    private val onWordChanged: (Int, String) -> Unit,
    private val onPhoneticChanged: (Int, String) -> Unit,
    private val onMeaningChanged: (Int, String) -> Unit,
    private val onExampleChanged: (Int, String) -> Unit,
    private val onNoteChanged: (Int, String) -> Unit,
    private val onVisualCueClick: (Int) -> Unit,
    private val onFetchPhoneticClick: (Int) -> Unit
) : ListAdapter<FlashcardEditUiItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

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
        fun bind(item: FlashcardEditUiItem) {
            val displayWord = item.card.word.ifBlank { "Từ mới #${item.originalIndex + 1}" }
            val displayPhonetic = item.card.phonetic.ifBlank { "..." }
            binding.tvIndex.text = (item.originalIndex + 1).toString()
            binding.tvWord.text = displayWord
            binding.tvPhonetic.text = displayPhonetic
            
            // For Delete vs Edit on collapsed... 
            // The collapsed view only has an edit button. It doesn't have a delete button.
            // Let's use btnEdit for toggle expand
            binding.btnEdit.setOnClickListener { onToggleExpand(item.originalIndex) }
            binding.root.setOnClickListener { onToggleExpand(item.originalIndex) }
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
        private val noteWatcher = simpleWatcher { onNoteChanged(currentIndex, it) }

        init {
            binding.btnDeleteCard.setOnClickListener { if (currentIndex >= 0) onDelete(currentIndex) }
            binding.btnUploadVisualCue.setOnClickListener { if (currentIndex >= 0) onVisualCueClick(currentIndex) }
            binding.btnAutoFetchPhonetic.setOnClickListener { if (currentIndex >= 0) onFetchPhoneticClick(currentIndex) }
        }

        fun bind(item: FlashcardEditUiItem) {
            currentIndex = item.originalIndex
            binding.tvCardIndex.text = "THẺ #${item.originalIndex + 1}"

            // Remove watchers before setting text
            binding.etWord.removeTextChangedListener(wordWatcher)
            binding.etPhonetic.removeTextChangedListener(phoneticWatcher)
            binding.etMeaning.removeTextChangedListener(meaningWatcher)
            binding.etExample.removeTextChangedListener(exampleWatcher)
            binding.etNote.removeTextChangedListener(noteWatcher)

            // Only set text if it differs to preserve cursor position
            if (binding.etWord.text.toString() != item.card.word) {
                binding.etWord.setText(item.card.word)
                binding.etWord.setSelection(binding.etWord.text?.length ?: 0)
            }
            if (binding.etPhonetic.text.toString() != item.card.phonetic) {
                binding.etPhonetic.setText(item.card.phonetic)
                binding.etPhonetic.setSelection(binding.etPhonetic.text?.length ?: 0)
            }
            if (binding.etMeaning.text.toString() != item.card.meaning) {
                binding.etMeaning.setText(item.card.meaning)
                binding.etMeaning.setSelection(binding.etMeaning.text?.length ?: 0)
            }
            if (binding.etExample.text.toString() != item.card.exampleSentence) {
                binding.etExample.setText(item.card.exampleSentence)
                binding.etExample.setSelection(binding.etExample.text?.length ?: 0)
            }
            if (binding.etNote.text.toString() != item.card.note) {
                binding.etNote.setText(item.card.note)
                binding.etNote.setSelection(binding.etNote.text?.length ?: 0)
            }

            // Show visual cue
            val visualUri = item.card.visualCueUri
            val visualUrl = item.card.visualCueUrl
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

            // Re-attach watchers
            binding.etWord.addTextChangedListener(wordWatcher)
            binding.etPhonetic.addTextChangedListener(phoneticWatcher)
            binding.etMeaning.addTextChangedListener(meaningWatcher)
            binding.etExample.addTextChangedListener(exampleWatcher)
            binding.etNote.addTextChangedListener(noteWatcher)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FlashcardEditUiItem>() {
            override fun areItemsTheSame(old: FlashcardEditUiItem, new: FlashcardEditUiItem) =
                old.originalIndex == new.originalIndex
            override fun areContentsTheSame(old: FlashcardEditUiItem, new: FlashcardEditUiItem) =
                old == new
        }
    }
}

private fun simpleWatcher(action: (String) -> Unit) = object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    override fun afterTextChanged(s: Editable?) { action(s?.toString() ?: "") }
}
