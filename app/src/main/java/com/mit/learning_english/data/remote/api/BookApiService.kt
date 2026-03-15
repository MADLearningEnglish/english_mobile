package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookHistory
import com.mit.learning_english.domain.model.Genre
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApiService {
    @GET("v1/books/genres/{genresId}")
    suspend fun getBooksByGenres(
        @Path("genresId") genresId: Int, @Query("page") page: Int, @Query("size") size: Int
    ): Response<BaseResponse<List<Book>>>

    @GET("v1/books/history")
    suspend fun getBookHistory(
        @Query("page") page: Int, @Query("size") size: Int
    ): Response<BaseResponse<List<BookHistory>>>

    @GET("api/book/v1/recommend")
    suspend fun getBooksRecommend(): Response<BaseResponse<List<Book>>>

    @GET("api/book/genres")
    suspend fun getGenres(): Response<BaseResponse<List<Genre>>>
}
