package com.mit.learning_english.presentation.feature.ai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemChatAssistantBinding
import com.mit.learning_english.databinding.ItemChatCorrectionBinding
import com.mit.learning_english.databinding.ItemChatPronunciationBinding
import com.mit.learning_english.databinding.ItemChatScenarioInlineBinding
import com.mit.learning_english.databinding.ItemChatUserBinding
import com.mit.learning_english.presentation.feature.ai.model.ChatListItem
import com.mit.learning_english.presentation.feature.ai.model.buildColoredText

class AiChatMessageAdapter(
    private val onScenarioDetails: (String) -> Unit,
    private val onSpeakAssistant: (String) -> Unit,
    private val onWhyCorrection: (String?) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ChatListItem>()

    fun submit(list: List<ChatListItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is ChatListItem.ScenarioCard -> VT_SCENARIO
        is ChatListItem.Assistant -> VT_ASSISTANT
        is ChatListItem.User -> VT_USER
        is ChatListItem.Correction -> VT_CORRECTION
        is ChatListItem.Pronunciation -> VT_PRONUNCIATION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VT_SCENARIO -> ScenarioVH(ItemChatScenarioInlineBinding.inflate(inflater, parent, false))
            VT_ASSISTANT -> AssistantVH(ItemChatAssistantBinding.inflate(inflater, parent, false))
            VT_USER -> UserVH(ItemChatUserBinding.inflate(inflater, parent, false))
            VT_CORRECTION -> CorrectionVH(ItemChatCorrectionBinding.inflate(inflater, parent, false))
            VT_PRONUNCIATION -> PronVH(ItemChatPronunciationBinding.inflate(inflater, parent, false))
            else -> error("Unknown type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ctx = holder.itemView.context
        when (val item = items[position]) {
            is ChatListItem.ScenarioCard -> (holder as ScenarioVH).bind(item, onScenarioDetails)
            is ChatListItem.Assistant -> (holder as AssistantVH).bind(item, onSpeakAssistant)
            is ChatListItem.User -> (holder as UserVH).bind(item)
            is ChatListItem.Correction -> (holder as CorrectionVH).bind(
                item,
                ContextCompat.getColor(ctx, R.color.quiz_wrong),
                ContextCompat.getColor(ctx, R.color.quiz_correct),
                onWhyCorrection,
            )
            is ChatListItem.Pronunciation -> (holder as PronVH).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class ScenarioVH(private val binding: ItemChatScenarioInlineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatListItem.ScenarioCard, onDetails: (String) -> Unit) {
            binding.scenarioBody.text = item.instruction
            binding.btnViewDetails.setOnClickListener { onDetails(item.instruction) }
        }
    }

    class AssistantVH(private val binding: ItemChatAssistantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatListItem.Assistant, onSpeak: (String) -> Unit) {
            binding.bubbleAssistant.text = item.content
            binding.btnSpeak.setOnClickListener { onSpeak(item.content) }
        }
    }

    class UserVH(private val binding: ItemChatUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatListItem.User) {
            binding.bubbleUser.text = item.content
        }
    }

    class CorrectionVH(private val binding: ItemChatCorrectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: ChatListItem.Correction,
            wrongColor: Int,
            suggestedColor: Int,
            onWhy: (String?) -> Unit,
        ) {
            binding.correctionText.text = item.buildColoredText(wrongColor, suggestedColor)
            val why = item.whyExplanation?.takeIf { it.isNotBlank() }
            binding.linkWhy.isVisible = why != null
            binding.linkWhy.setOnClickListener { onWhy(why) }
        }
    }

    class PronVH(private val binding: ItemChatPronunciationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatListItem.Pronunciation) {
            binding.pronunciationText.text = binding.root.context.getString(
                R.string.ai_pronunciation_score_format,
                item.scoreOutOf100,
                item.subtitle,
            )
        }
    }

    companion object {
        private const val VT_SCENARIO = 1
        private const val VT_ASSISTANT = 2
        private const val VT_USER = 3
        private const val VT_CORRECTION = 4
        private const val VT_PRONUNCIATION = 5
    }
}
