package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.FileDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileApiService {
    @Multipart
    @POST("/api/file/v1/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): Response<BaseResponse<FileDto>>
}
