package com.mit.learning_english.data.mapper

import com.mit.learning_english.data.remote.dto.AudioResponse
import com.mit.learning_english.data.remote.dto.PageResponse
import com.mit.learning_english.data.remote.dto.SentenceResponse
import com.mit.learning_english.data.remote.dto.TextLookupResponseDto
import com.mit.learning_english.domain.model.Audio
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.model.Sentence
import com.mit.learning_english.domain.model.TextLookupResult

fun PageResponse.toPage(): Page {
    return Page(
        id = id,
        number = number,
        audio = audio.toAudio(),
        sentences = sentence.map { it.toSentence() }
    )
}

fun AudioResponse.toAudio(): Audio {
    return Audio(
        id = id,
        duration = duration,
        format = format,
        sampleRate = sampleRate,
        fileSize = fileSize,
        fileUrl = fileUrl,
        pagesId = pagesId
    )
}

fun SentenceResponse.toSentence(): Sentence {
    return Sentence(
        pagesId = pagesId,
        id = id,
        content = content,
        transcription1 = transcription1,
        startTime = startTime,
        endTime = endTime
    )
}

fun TextLookupResponseDto.toTextLookupResult(): TextLookupResult {
    return TextLookupResult(
        selectedText = selectedText,
        meaning = meaning,
        phonetic = phonetic,
        audioUrl = audioUrl,
        examples = examples.orEmpty()
    )
}
