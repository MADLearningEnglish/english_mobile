package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.BookResponse
import com.mit.learning_english.data.remote.dto.BookReadingProgressRequestDto
import com.mit.learning_english.data.remote.dto.PaginatedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApiService {
    @GET("v1/books/genres/{genresId}")
    suspend fun getBooksByGenres(
        @Path("genresId") genresId: Int, @Query("page") page: Int, @Query("size") size: Int
    ): Response<BaseResponse<List<BookResponse>>>

    @GET("book/v1/history")
    suspend fun getBookHistory(
        @Query("page") page: Int, @Query("limit") limit: Int
    ): Response<BaseResponse<PaginatedResponse<BookResponse>>>

    @GET("book/v1/recommend")
    suspend fun getBooksRecommend(): Response<BaseResponse<List<BookResponse>>>

    @GET("book/v1/recommend/by-topic")
    suspend fun getRecommendByTopic(): Response<BaseResponse<List<BookResponse>>>

    @GET("book/v1/recommend/by-author")
    suspend fun getRecommendByAuthor(): Response<BaseResponse<List<BookResponse>>>

    @GET("book/v1/reading/in-progress")
    suspend fun getReadingInProgress(): Response<BaseResponse<List<BookResponse>>>

    @GET("book/v1/{id}")
    suspend fun getBookDetailById(@Path("id") bookId: Int): Response<BaseResponse<BookResponse>>

    @GET("api/books/{bookId}")
    suspend fun updateFavoriteBook(@Path("bookId") bookId: Int)

    @PUT("book/{bookId}")
    suspend fun updateFavoriteBook(
        @Path("bookId") bookId: Int, @Query("isFavorite") isFavorite: Boolean
    ): Response<BaseResponse<Boolean>>

    @GET("book/v1")
    suspend fun searchBooks(
        @Query("keyword") keyword: String, @Query("page") page: Int, @Query("limit") limit: Int
    ): Response<BaseResponse<PaginatedResponse<BookResponse>>>

    @PATCH("book/v1/{bookId}/progress")
    suspend fun updateReadingProgress(
        @Path("bookId") bookId: Int,
        @Body body: BookReadingProgressRequestDto
    ): Response<BaseResponse<String>>

}
