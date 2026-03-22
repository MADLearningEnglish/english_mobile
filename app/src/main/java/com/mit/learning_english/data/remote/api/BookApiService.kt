package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.model.BookHistory
import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.domain.model.Page
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

    @GET("api/books/{bookId}")
    suspend fun getBookDetailById(@Path("bookId") bookId: Int): Response<BaseResponse<BookDetail>>

    @GET("api/books/{bookId}")
    suspend fun updateFavoriteBook(@Path("bookId") bookId: Int)

    @GET("api/books/{bookId}/pages")
    suspend fun getPagesByChapter(
        @Path("bookId") bookId: Int,
        @Query("pageNumbers") pageNumbers: List<Int>,
    ): Response<BaseResponse<List<Page>>>
}
