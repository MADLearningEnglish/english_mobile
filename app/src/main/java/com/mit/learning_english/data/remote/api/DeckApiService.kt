package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.CreateDeckRequestDto
import com.mit.learning_english.data.remote.dto.DeckDto
import com.mit.learning_english.data.remote.dto.FlashcardDto
import com.mit.learning_english.data.remote.dto.StudyResultDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface DeckApiService {
    @POST("/api/deck/v1")
    suspend fun createDeck(@Body request: CreateDeckRequestDto): Response<BaseResponse<DeckDto>>

    @GET("/api/deck/v1")
    suspend fun getAllDecks(): Response<BaseResponse<List<DeckDto>>>

    @GET("/api/deck/v1/{id}")
    suspend fun getDeckById(@Path("id") deckId: Int): Response<BaseResponse<DeckDto>>

    @PUT("/api/deck/v1/{id}")
    suspend fun updateDeck(
        @Path("id") deckId: Int,
        @Body request: com.mit.learning_english.data.remote.dto.UpdateDeckRequestDto
    ): Response<BaseResponse<DeckDto>>

    @GET("/api/deck/v1/{id}/study")
    suspend fun getFlashcardsToStudy(@Path("id") deckId: Int): Response<BaseResponse<List<FlashcardDto>>>

    @GET("/api/deck/v1/{id}/flashcards")
    suspend fun getAllFlashcards(@Path("id") deckId: Int): Response<BaseResponse<List<FlashcardDto>>>

    @POST("/api/deck/v1/{id}/flashcards/{flashcardId}/review")
    suspend fun reviewFlashcard(
        @Path("id") deckId: Int,
        @Path("flashcardId") flashcardId: Int,
        @Query("level") level: String
    ): Response<BaseResponse<String>>

    @GET("/api/deck/v1/{id}/results")
    suspend fun getStudyResults(@Path("id") deckId: Int): Response<BaseResponse<StudyResultDto>>

    @DELETE("/api/deck/v1/{id}")
    suspend fun deleteDeck(@Path("id") deckId: Int): Response<BaseResponse<String>>
}