package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.PageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PageApiService {
    @GET("book/v1/{bookId}/pages")
    suspend fun getPagesByBook(
        @Path("bookId") bookId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Response<BaseResponse<List<PageResponse>>>
}
