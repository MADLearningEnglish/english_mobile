package com.mit.learning_english.data.mapper

import com.mit.learning_english.data.remote.dto.CreateDeckRequestDto
import com.mit.learning_english.data.remote.dto.DeckDto
import com.mit.learning_english.data.remote.dto.FlashcardCreateDto
import com.mit.learning_english.data.remote.dto.FlashcardDto
import com.mit.learning_english.data.remote.dto.StudyResultDto
import com.mit.learning_english.domain.model.CreateDeckRequest
import com.mit.learning_english.domain.model.Deck
import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.domain.model.FlashcardInput
import com.mit.learning_english.domain.model.StudyResult

fun DeckDto.toDomain(): Deck {
    return Deck(
        id = id,
        title = title,
        coverImageUrl = coverImageUrl,
        totalWords = totalWords,
        status = status,
        flashcards = flashcards?.map { it.toDomain() } ?: emptyList()
    )
}

fun FlashcardDto.toDomain(): Flashcard {
    return Flashcard(
        id = id,
        word = word,
        phonetic = phonetic,
        meaning = meaning,
        partOfSpeech = partOfSpeech,
        exampleSentence = exampleSentence,
        note = note,
        visualCueUrl = visualCueUrl
    )
}

fun StudyResultDto.toDomain(): StudyResult {
    return StudyResult(totalWords, masteryPercentage, knownCount, easyCount, mediumCount, hardCount)
}

fun FlashcardInput.toDto(): FlashcardCreateDto {
    return FlashcardCreateDto(
        word = word,
        phonetic = phonetic.ifBlank { null },
        meaning = meaning,
        exampleSentence = exampleSentence.ifBlank { null },
        visualCueUrl = visualCueUrl,
        note = note.ifBlank { null }
    )
}

fun CreateDeckRequest.toDto(): CreateDeckRequestDto {
    return CreateDeckRequestDto(
        title = title,
        coverImageUrl = coverImageUrl,
        flashcards = flashcards.map { it.toDto() }
    )
}

fun com.mit.learning_english.domain.model.FlashcardUpdateInput.toDto(): com.mit.learning_english.data.remote.dto.FlashcardUpdateDto {
    return com.mit.learning_english.data.remote.dto.FlashcardUpdateDto(
        id = id,
        word = word,
        phonetic = phonetic.ifBlank { null },
        meaning = meaning,
        exampleSentence = exampleSentence.ifBlank { null },
        visualCueUrl = visualCueUrl,
        note = note.ifBlank { null },
        status = status
    )
}

fun com.mit.learning_english.domain.model.UpdateDeckRequest.toDto(): com.mit.learning_english.data.remote.dto.UpdateDeckRequestDto {
    return com.mit.learning_english.data.remote.dto.UpdateDeckRequestDto(
        title = title,
        coverImageUrl = coverImageUrl,
        status = status,
        flashcards = flashcards.map { it.toDto() }
    )
}