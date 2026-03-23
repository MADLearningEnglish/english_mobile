package com.mit.learning_english.data.mapper

import com.mit.learning_english.data.remote.dto.AudioResponse
import com.mit.learning_english.data.remote.dto.PageResponse
import com.mit.learning_english.data.remote.dto.SentenceResponse
import com.mit.learning_english.domain.model.Audio
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.model.Sentence

fun PageResponse.toPage(): Page {
    return Page(
        id = id,
        number = number,
        audio = audio.toAudio(),
        sentence = sentence.map { it.toSentence() }
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
