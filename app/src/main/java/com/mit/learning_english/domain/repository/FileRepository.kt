package com.mit.learning_english.domain.repository

import com.mit.learning_english.data.remote.dto.FileDto
import com.mit.learning_english.domain.util.Result
import okhttp3.MultipartBody

interface FileRepository {
    suspend fun uploadFile(file: MultipartBody.Part): Result<FileDto>
}
