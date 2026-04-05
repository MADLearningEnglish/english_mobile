package com.mit.learning_english.presentation.feature.ai

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mit.learning_english.R
import com.mit.learning_english.data.remote.dto.AiScenarioDto
import com.mit.learning_english.databinding.ItemScenarioCardBinding

class ScenarioAdapter(
    private val onClick: (AiScenarioDto) -> Unit,
) : ListAdapter<AiScenarioDto, ScenarioAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemScenarioCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemScenarioCardBinding,
        private val onClick: (AiScenarioDto) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AiScenarioDto) {
            binding.title.text = item.title.orEmpty()
            binding.description.text = item.description.orEmpty()
            val level = item.levelName.orEmpty()
            binding.levelBadge.text = level.ifBlank { binding.root.context.getString(R.string.ai_difficulty_beginner) }
            styleLevelBadge(level)
            bindIcon(item)
            binding.root.setOnClickListener { onClick(item) }
        }

        private fun styleLevelBadge(level: String) {
            val ctx = binding.root.context
            val density = ctx.resources.displayMetrics.density
            val l = level.lowercase()
            val (bg, fg) = when {
                l.contains("advanced") || l.contains("hard") ->
                    ContextCompat.getColor(ctx, R.color.difficulty_hard_bg) to
                        ContextCompat.getColor(ctx, R.color.difficulty_hard)
                l.contains("intermediate") || l.contains("medium") ->
                    ContextCompat.getColor(ctx, R.color.ai_level_intermediate_bg) to
                        ContextCompat.getColor(ctx, R.color.ai_level_intermediate_text)
                else ->
                    ContextCompat.getColor(ctx, R.color.difficulty_easy_bg) to
                        ContextCompat.getColor(ctx, R.color.difficulty_easy)
            }
            val pill = GradientDrawable().apply {
                cornerRadius = 20f * density
                setColor(bg)
            }
            binding.levelBadge.background = pill
            binding.levelBadge.setTextColor(fg)
        }

        private fun bindIcon(item: AiScenarioDto) {
            val url = item.iconUrl?.takeIf { it.isNotBlank() }
            if (url != null) {
                binding.iconEmoji.visibility = android.view.View.GONE
                binding.iconImage.visibility = android.view.View.VISIBLE
                Glide.with(binding.iconImage).load(url).fitCenter().into(binding.iconImage)
            } else {
                binding.iconImage.visibility = android.view.View.GONE
                binding.iconEmoji.visibility = android.view.View.VISIBLE
                binding.iconEmoji.text = emojiForScenario(item.title.orEmpty())
            }
        }

        private fun emojiForScenario(title: String): String {
            val t = title.lowercase()
            return when {
                t.contains("coffee") || t.contains("café") || t.contains("cafe") || t.contains("order") -> "☕"
                t.contains("interview") || t.contains("job") -> "💼"
                t.contains("direct") || t.contains("way") || t.contains("map") -> "🧭"
                t.contains("introduc") || t.contains("yourself") -> "👋"
                t.contains("debate") || t.contains("issue") -> "⚖️"
                else -> "💬"
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<AiScenarioDto>() {
            override fun areItemsTheSame(a: AiScenarioDto, b: AiScenarioDto) = a.id == b.id
            override fun areContentsTheSame(a: AiScenarioDto, b: AiScenarioDto) = a == b
        }
    }
}
