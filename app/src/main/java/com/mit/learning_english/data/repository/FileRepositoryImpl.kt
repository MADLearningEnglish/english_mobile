package com.mit.learning_english.data.repository

import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.FileApiService
import com.mit.learning_english.data.remote.dto.FileDto
import com.mit.learning_english.domain.repository.FileRepository
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.shared.UiErrorKey
import okhttp3.MultipartBody
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val apiService: FileApiService,
    private val resultMapper: ResultMapper
) : FileRepository {
    override suspend fun uploadFile(file: MultipartBody.Part): Result<FileDto> {
        return try {
            val response = apiService.uploadFile(file)
            when (val result = resultMapper.fromResponse(response)) {
                is Result.Success -> {
                    val fileDto = result.data.data
                    if (fileDto != null) {
                        Result.Success(fileDto)
                    } else {
                        Result.Error(result.data.message ?: UiErrorKey.UPLOAD_FAILED)
                    }
                }
                is Result.Error -> result
                else -> Result.Error(UiErrorKey.UNKNOWN)
            }
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }
}
