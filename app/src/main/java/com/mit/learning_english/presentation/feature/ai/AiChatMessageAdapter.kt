package com.mit.learning_english.presentation.feature.ai

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
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

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil

class AiChatMessageAdapter(
    private val onScenarioDetails: (String) -> Unit,
    private val onSpeakAssistant: (String) -> Unit,
    private val onWhyCorrection: (String?) -> Unit,
    private val onTextAction: (SelectionAction, String) -> Unit,
) : ListAdapter<ChatListItem, RecyclerView.ViewHolder>(DiffCallback) {

    enum class SelectionAction { TRANSLATE, LISTEN, COPY, SAVE }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
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
        when (val item = getItem(position)) {
            is ChatListItem.ScenarioCard -> (holder as ScenarioVH).bind(item, onScenarioDetails)
            is ChatListItem.Assistant -> (holder as AssistantVH).bind(item, onSpeakAssistant, onTextAction)
            is ChatListItem.User -> (holder as UserVH).bind(item, onTextAction)
            is ChatListItem.Correction -> (holder as CorrectionVH).bind(
                item,
                ContextCompat.getColor(ctx, R.color.quiz_wrong),
                ContextCompat.getColor(ctx, R.color.quiz_correct),
                onWhyCorrection,
            )
            is ChatListItem.Pronunciation -> (holder as PronVH).bind(item)
        }
    }

    class ScenarioVH(private val binding: ItemChatScenarioInlineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatListItem.ScenarioCard, onDetails: (String) -> Unit) {
            binding.scenarioBody.text = item.instruction
            binding.btnViewDetails.setOnClickListener { onDetails(item.instruction) }
        }
    }

    class AssistantVH(private val binding: ItemChatAssistantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: ChatListItem.Assistant,
            onSpeak: (String) -> Unit,
            onTextAction: (SelectionAction, String) -> Unit,
        ) {
            binding.bubbleAssistant.text = renderBoldMarkdown(item.content)
            installSelectionMenu(
                binding.bubbleAssistant,
                selectionAction = onSpeak,
                onTextAction = onTextAction,
            )
            binding.btnSpeak.setOnClickListener { onSpeak(stripBoldMarkdown(item.content)) }
        }

        private var onTextAction: ((SelectionAction, String) -> Unit)? = null
        private fun installSelectionMenu(
            textView: TextView,
            selectionAction: (String) -> Unit,
            onTextAction: (SelectionAction, String) -> Unit,
        ) {
            this.onTextAction = onTextAction
            textView.customSelectionActionModeCallback = buildSelectionCallback(textView, selectionAction)
        }

        private fun renderBoldMarkdown(raw: String): CharSequence {
            if (!raw.contains("**")) return raw

            val regex = Regex("\\*\\*(.+?)\\*\\*")
            val sb = SpannableStringBuilder()
            var cursor = 0
            for (match in regex.findAll(raw)) {
                val start = match.range.first
                val end = match.range.last + 1
                if (start > cursor) {
                    sb.append(raw.substring(cursor, start))
                }
                val boldContent = match.groupValues[1]
                val boldStart = sb.length
                sb.append(boldContent)
                sb.setSpan(
                    StyleSpan(Typeface.BOLD),
                    boldStart,
                    sb.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                cursor = end
            }
            if (cursor < raw.length) {
                sb.append(raw.substring(cursor))
            }
            return sb
        }

        private fun stripBoldMarkdown(raw: String): String {
            return raw.replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
        }

        private fun buildSelectionCallback(
            textView: TextView,
            onListen: (String) -> Unit,
        ): ActionMode.Callback {
            val idTranslate = 8001
            val idListen = 8002
            val idCopy = 8003
            val idSave = 8004
            return object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    menu?.clear()
                    menu?.add(0, idTranslate, 0, textView.context.getString(R.string.ai_action_translate))
                    menu?.add(0, idListen, 1, textView.context.getString(R.string.ai_listen))
                    menu?.add(0, idCopy, 2, textView.context.getString(R.string.ai_action_copy))
                    menu?.add(0, idSave, 3, textView.context.getString(R.string.ai_action_save))
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    val selected = getSelectedText(textView)
                    if (selected.isBlank()) return false
                    when (item?.itemId) {
                        idTranslate -> onTextAction?.invoke(SelectionAction.TRANSLATE, selected)
                        idListen -> onListen(selected)
                        idCopy -> onTextAction?.invoke(SelectionAction.COPY, selected)
                        idSave -> onTextAction?.invoke(SelectionAction.SAVE, selected)
                        else -> return false
                    }
                    mode?.finish()
                    return true
                }

                override fun onDestroyActionMode(mode: ActionMode?) = Unit
            }
        }

        private fun getSelectedText(textView: TextView): String {
            val start = textView.selectionStart
            val end = textView.selectionEnd
            if (start < 0 || end < 0 || start == end) return ""
            val min = minOf(start, end)
            val max = maxOf(start, end)
            return textView.text?.substring(min, max).orEmpty()
        }
    }

    class UserVH(private val binding: ItemChatUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatListItem.User, onTextAction: (SelectionAction, String) -> Unit) {
            binding.bubbleUser.text = item.content
            binding.bubbleUser.customSelectionActionModeCallback =
                buildSelectionCallback(binding.bubbleUser, onTextAction)
        }

        private fun buildSelectionCallback(
            textView: TextView,
            onTextAction: (SelectionAction, String) -> Unit,
        ): ActionMode.Callback {
            val idTranslate = 8101
            val idListen = 8102
            val idCopy = 8103
            val idSave = 8104
            return object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    menu?.clear()
                    menu?.add(0, idTranslate, 0, textView.context.getString(R.string.ai_action_translate))
                    menu?.add(0, idListen, 1, textView.context.getString(R.string.ai_listen))
                    menu?.add(0, idCopy, 2, textView.context.getString(R.string.ai_action_copy))
                    menu?.add(0, idSave, 3, textView.context.getString(R.string.ai_action_save))
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    val selected = getSelectedText(textView)
                    if (selected.isBlank()) return false
                    when (item?.itemId) {
                        idTranslate -> onTextAction.invoke(SelectionAction.TRANSLATE, selected)
                        idListen -> onTextAction.invoke(SelectionAction.LISTEN, selected)
                        idCopy -> onTextAction.invoke(SelectionAction.COPY, selected)
                        idSave -> onTextAction.invoke(SelectionAction.SAVE, selected)
                        else -> return false
                    }
                    mode?.finish()
                    return true
                }

                override fun onDestroyActionMode(mode: ActionMode?) = Unit
            }
        }

        private fun getSelectedText(textView: TextView): String {
            val start = textView.selectionStart
            val end = textView.selectionEnd
            if (start < 0 || end < 0 || start == end) return ""
            val min = minOf(start, end)
            val max = maxOf(start, end)
            return textView.text?.substring(min, max).orEmpty()
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

        private val DiffCallback = object : DiffUtil.ItemCallback<ChatListItem>() {
            override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
