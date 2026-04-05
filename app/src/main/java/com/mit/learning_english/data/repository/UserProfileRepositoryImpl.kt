package com.mit.learning_english.data.repository

import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.UserProfileApiService
import com.mit.learning_english.data.remote.dto.BaseResponse
import com.mit.learning_english.data.remote.dto.UpdateUserFavoriteGenresRequest
import com.mit.learning_english.data.remote.dto.UpdateUserLevelRequest
import com.mit.learning_english.domain.repository.UserProfileRepository
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileApiService: UserProfileApiService,
    private val resultMapper: ResultMapper
) : UserProfileRepository {

    override suspend fun updateLevel(levelId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
             val result = userProfileApiService.updateLevel(UpdateUserLevelRequest(levelId))
             resultMapper.fromBaseResponse(result)
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }

    override suspend fun updateFavoriteGenres(genreIds: List<Int>): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                   val result =  userProfileApiService.updateFavoriteGenres(
                        UpdateUserFavoriteGenresRequest(genreIds)
                    )
                resultMapper.fromBaseResponse(result)

            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
}
