package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.BookResponse
import com.mit.learning_english.data.remote.dto.PageResponse
import com.mit.learning_english.data.remote.dto.PaginatedResponse
import retrofit2.Response
import retrofit2.http.GET
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

    @GET("api/books/{bookId}")
    suspend fun getBookDetailById(@Path("bookId") bookId: Int): Response<BaseResponse<BookResponse>>

    @GET("api/books/{bookId}")
    suspend fun updateFavoriteBook(@Path("bookId") bookId: Int)

    @GET("api/books/{bookId}/pages")
    suspend fun getPagesReadBook(
        @Path("bookId") bookId: Int,
        @Query("pageNumbers") pageNumbers: List<Int>,
    ): Response<BaseResponse<List<PageResponse>>>

    @PUT("api/books/{bookId}")
    suspend fun updateFavoriteBook(
        @Path("bookId") bookId: Int, @Query("isFavorite") isFavorite: Boolean
    ): Response<BaseResponse<Boolean>>

    @GET("book/v1")
    suspend fun searchBooks(
        @Query("keyword") keyword: String, @Query("page") page: Int, @Query("limit") limit: Int
    ): Response<BaseResponse<PaginatedResponse<BookResponse>>>

}
