package com.mit.learning_english.data.repository

import com.mit.learning_english.data.mapper.toDomain
import com.mit.learning_english.data.mapper.toDto
import com.mit.learning_english.data.remote.dto.DeckStudyCompleteRequestDto
import com.mit.learning_english.data.remote.api.DeckApiService
import com.mit.learning_english.domain.model.*
import com.mit.learning_english.domain.repository.DeckRepository
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeckRepositoryImpl @Inject constructor(
    private val apiService: DeckApiService
) : DeckRepository {

    override suspend fun createDeck(request: CreateDeckRequest): Result<Deck> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createDeck(request.toDto())
            if (response.isSuccessful && response.body()?.data != null) {
                Result.Success(response.body()!!.data!!.toDomain())
            } else {
                Result.Error(response.message() ?: "Lỗi tạo bộ thẻ")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getAllDecks(): Result<List<Deck>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllDecks()
            if (response.isSuccessful) {
                // Quá trình mapping toDomain() giờ đây đã an toàn trên luồng nền (IO)
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(data)
            } else {
                Result.Error(response.message() ?: "Lỗi tải danh sách bộ thẻ")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getFlashcardsToStudy(deckId: Int): Result<List<Flashcard>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFlashcardsToStudy(deckId)
            if (response.isSuccessful) {
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(data)
            } else {
                Result.Error("Failed to fetch flashcards")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getAllFlashcards(deckId: Int): Result<List<Flashcard>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllFlashcards(deckId)
            if (response.isSuccessful) {
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(data)
            } else {
                Result.Error("Failed to fetch flashcards")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun reviewFlashcard(deckId: Int, flashcardId: Int, level: MasteryLevel): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.reviewFlashcard(deckId, flashcardId, level.name)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Review failed")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getStudyResults(deckId: Int): Result<StudyResult> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getStudyResults(deckId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.Success(response.body()!!.data!!.toDomain())
            } else {
                Result.Error("Cannot get results")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getDeckById(deckId: Int): Result<Deck> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getDeckById(deckId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.Success(response.body()!!.data!!.toDomain())
            } else {
                Result.Error(response.message() ?: "Lỗi tải thông tin bộ thẻ")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateDeck(deckId: Int, request: com.mit.learning_english.domain.model.UpdateDeckRequest): Result<Deck> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateDeck(deckId, request.toDto())
            if (response.isSuccessful && response.body()?.data != null) {
                Result.Success(response.body()!!.data!!.toDomain())
            } else {
                Result.Error(response.message() ?: "Lỗi cập nhật bộ thẻ")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun deleteDeck(deckId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteDeck(deckId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Delete failed")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun postStudyComplete(
        deckId: Int,
        durationSeconds: Int,
        cardsReviewed: Int?,
        quizCorrect: Int?,
        quizTotal: Int?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val body = DeckStudyCompleteRequestDto(
                durationSeconds = durationSeconds,
                cardsReviewed = cardsReviewed,
                quizCorrect = quizCorrect,
                quizTotal = quizTotal
            )
            val response = apiService.postStudyComplete(deckId, body)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message() ?: "study-complete failed")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}