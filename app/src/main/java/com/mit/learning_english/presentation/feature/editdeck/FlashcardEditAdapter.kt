package com.mit.learning_english.presentation.feature.editdeck

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.databinding.ItemFlashcardInputCardBinding
import com.mit.learning_english.domain.model.FlashcardUpdateInput

data class FlashcardEditUiItem(
    val card: FlashcardUpdateInput,
    val originalIndex: Int
)

class FlashcardEditAdapter(
    private val onTermChanged: (Int, String) -> Unit,
    private val onDefinitionChanged: (Int, String) -> Unit,
    private val onImagePickRequested: (Int) -> Unit,
    private val onDeleteRequested: (Int) -> Unit
) : ListAdapter<FlashcardEditUiItem, FlashcardEditAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemFlashcardInputCardBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemFlashcardInputCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentIndex = -1
        private val termWatcher = simpleWatcher { onTermChanged(currentIndex, it) }
        private val definitionWatcher = simpleWatcher { onDefinitionChanged(currentIndex, it) }

        fun bind(item: FlashcardEditUiItem) {
            currentIndex = item.originalIndex

            binding.etWord.removeTextChangedListener(termWatcher)
            binding.etMeaning.removeTextChangedListener(definitionWatcher)

            if (binding.etWord.text.toString() != item.card.term) {
                binding.etWord.setText(item.card.term)
                binding.etWord.setSelection(binding.etWord.text?.length ?: 0)
            }
            if (binding.etMeaning.text.toString() != item.card.definition) {
                binding.etMeaning.setText(item.card.definition)
                binding.etMeaning.setSelection(binding.etMeaning.text?.length ?: 0)
            }

            binding.etWord.addTextChangedListener(termWatcher)
            binding.etMeaning.addTextChangedListener(definitionWatcher)

            binding.btnAddPhoto.setOnClickListener { onImagePickRequested(currentIndex) }
            binding.btnDeleteCard.setOnClickListener { onDeleteRequested(currentIndex) }

            val imageToLoad = item.card.imageUri?.toString() ?: com.mit.learning_english.shared.MediaUrlResolver.resolve(item.card.imageUrl)
            if (imageToLoad != null) {
                binding.cvImagePreview.visibility = android.view.View.VISIBLE
                com.bumptech.glide.Glide.with(binding.root.context)
                    .load(imageToLoad)
                    .centerCrop()
                    .into(binding.imgPreview)
            } else {
                binding.cvImagePreview.visibility = android.view.View.GONE
            }
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
