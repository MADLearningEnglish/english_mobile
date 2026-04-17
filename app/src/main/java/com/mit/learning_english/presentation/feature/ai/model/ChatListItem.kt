package com.mit.learning_english.presentation.feature.ai.model

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import com.mit.learning_english.data.remote.dto.ChatMessageDetailItemDto
import com.mit.learning_english.data.remote.dto.FeedbackDto

sealed class ChatListItem {
    data class ScenarioCard(val instruction: String) : ChatListItem()

    data class Assistant(val content: String) : ChatListItem()

    data class User(val content: String) : ChatListItem()

    data class Correction(
        val wrongText: String,
        val suggestedText: String,
        val explanationAfter: String?,
        val whyExplanation: String?,
    ) : ChatListItem()

    data class Pronunciation(val scoreOutOf100: Int, val subtitle: String) : ChatListItem()
}

fun FeedbackDto.toPronunciationScore100(): Int {
    val p = pronunciationScore ?: return 0
    return if (p <= 10.5) (p * 10).toInt().coerceIn(0, 100) else p.toInt().coerceIn(0, 100)
}

/** Pronunciation score chỉ áp dụng khi người dùng gửi bằng giọng nói, không phải khi gõ text. */
fun shouldShowPronunciationForInput(inputType: String?): Boolean =
    inputType?.equals("VOICE", ignoreCase = true) == true

fun FeedbackDto.toCorrectionItem(): ChatListItem.Correction? {
    val errList = errors
    if (!errList.isNullOrEmpty()) {
        val e = errList.first()
        val orig = e.originalText.orEmpty()
        val sug = e.suggestedText.orEmpty()
        if (orig.isEmpty() && sug.isEmpty()) return null
        val after = e.explanation?.takeIf { it.isNotBlank() }
        return ChatListItem.Correction(
            wrongText = orig,
            suggestedText = sug,
            explanationAfter = after,
            whyExplanation = e.explanation,
        )
    }
    val improved = improvedVersion?.trim().orEmpty()
    if (improved.isNotEmpty()) {
        return ChatListItem.Correction(
            wrongText = "",
            suggestedText = improved,
            explanationAfter = null,
            whyExplanation = null,
        )
    }
    val natural = naturalSuggestion?.trim().orEmpty()
    if (natural.isNotEmpty()) {
        return ChatListItem.Correction(
            wrongText = "",
            suggestedText = natural,
            explanationAfter = null,
            whyExplanation = null,
        )
    }
    return null
}

fun ChatListItem.Correction.buildColoredText(wrongColor: Int, suggestedColor: Int): CharSequence {
    if (wrongText.isEmpty() && suggestedText.isNotEmpty()) {
        val sb = SpannableStringBuilder(suggestedText)
        sb.setSpan(StyleSpan(Typeface.BOLD), 0, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.setSpan(ForegroundColorSpan(suggestedColor), 0, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }
    val sb = SpannableStringBuilder()
    val headerStart = sb.length
    sb.append("LOI SAI CAN NHO: ")
    sb.setSpan(StyleSpan(Typeface.BOLD), headerStart, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    sb.setSpan(ForegroundColorSpan(wrongColor), headerStart, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    sb.append("\n")
    sb.append("Ban viet: ")
    val wStart = sb.length
    sb.append(wrongText)
    sb.setSpan(ForegroundColorSpan(wrongColor), wStart, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    sb.setSpan(StyleSpan(Typeface.BOLD), wStart, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    sb.append("\nGoi y dung: ")
    val sStart = sb.length
    sb.append(suggestedText)
    sb.setSpan(ForegroundColorSpan(suggestedColor), sStart, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    sb.setSpan(StyleSpan(Typeface.BOLD), sStart, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    explanationAfter?.takeIf { it.isNotBlank() }?.let {
        sb.append("\nGiai thich: ")
        sb.append(it)
    }
    return sb
}

fun mapTranscriptToChatItems(
    instruction: String,
    transcript: List<ChatMessageDetailItemDto>,
): List<ChatListItem> = buildList {
    if (instruction.isNotBlank()) {
        add(ChatListItem.ScenarioCard(instruction))
    }
    val hasOpeningAi = transcript.any {
        it.senderType?.equals("AI", ignoreCase = true) == true && !it.content.isNullOrBlank()
    }
    if (!hasOpeningAi) {
        add(ChatListItem.Assistant(buildFallbackOpening(instruction)))
    }
    for (m in transcript) {
        when (m.senderType?.uppercase()) {
            "AI" -> add(ChatListItem.Assistant(m.content.orEmpty()))
            "USER" -> {
                add(ChatListItem.User(m.content.orEmpty()))
                val fb = m.feedback ?: continue
                fb.toCorrectionItem()?.let { add(it) }
                if (shouldShowPronunciationForInput(m.inputType)) {
                    val score = fb.toPronunciationScore100()
                    if (score > 0 || fb.pronunciationScore != null) {
                        val label = when {
                            score >= 80 -> "Good!"
                            score >= 60 -> "Fair"
                            else -> "Keep practicing"
                        }
                        add(ChatListItem.Pronunciation(score, label))
                    }
                }
            }
        }
    }
}

private fun buildFallbackOpening(instruction: String): String {
    val normalized = instruction.trim()
    if (normalized.isNotBlank()) {
        return "Hi! Let's start. $normalized"
    }
    return "Hi! Nice to meet you. What would you like to talk about today?"
}

fun appendSendResponseItems(
    feedback: FeedbackDto?,
    aiContent: String,
    userInputType: String?,
): List<ChatListItem> = buildList {
    val fb = feedback
    if (fb != null) {
        fb.toCorrectionItem()?.let { add(it) }
        if (shouldShowPronunciationForInput(userInputType)) {
            if (fb.pronunciationScore != null || fb.toPronunciationScore100() > 0) {
                val s = fb.toPronunciationScore100()
                val label = when {
                    s >= 80 -> "Good!"
                    s >= 60 -> "Fair"
                    else -> "Keep practicing"
                }
                add(ChatListItem.Pronunciation(s, label))
            }
        }
    }
    add(ChatListItem.Assistant(aiContent))
}
