package com.mit.learning_english.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.mapper.toActivityDayDetail
import com.mit.learning_english.data.mapper.toDomain
import com.mit.learning_english.data.mapper.toVocabularyWord
import com.mit.learning_english.data.paging.CorrectionsPagingSource
import com.mit.learning_english.data.paging.LearningActivityHistoryPagingSource
import com.mit.learning_english.data.paging.VocabularyPagingSource
import com.mit.learning_english.data.remote.api.ProfileApiService
import com.mit.learning_english.data.remote.dto.ProfilePatchRequestDto
import com.mit.learning_english.data.remote.dto.UserLearnedWordCreateDto
import com.mit.learning_english.data.remote.dto.UserLearnedWordPatchDto
import com.mit.learning_english.domain.model.profile.ActivityDayDetail
import com.mit.learning_english.domain.model.profile.CorrectionSessionReview
import com.mit.learning_english.domain.model.profile.HeatmapDay
import com.mit.learning_english.domain.model.profile.LearningActivityItem
import com.mit.learning_english.domain.model.profile.UserCorrectionItem
import com.mit.learning_english.domain.model.profile.LearningStatsOverview
import com.mit.learning_english.domain.model.profile.ProfileMe
import com.mit.learning_english.domain.model.profile.VocabularyWord
import com.mit.learning_english.domain.repository.ProfileRepository
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter
class ProfileRepositoryImpl(
    private val api: ProfileApiService,
    private val resultMapper: ResultMapper
) : ProfileRepository {

    private val isoDate = DateTimeFormatter.ISO_LOCAL_DATE

    override suspend fun getMe(): Result<ProfileMe> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMe()
            resultMapper.fromBaseResponse(response).map { it.toDomain() }
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }

    override suspend fun patchMe(
        fullName: String?,
        location: String?,
        learningLevel: String?
    ): Result<ProfileMe> = withContext(Dispatchers.IO) {
        try {
            val response = api.patchMe(
                ProfilePatchRequestDto(
                    fullName = fullName,
                    location = location,
                    learningLevel = learningLevel
                )
            )
            resultMapper.fromBaseResponse(response).map { it.toDomain() }
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }

    override suspend fun uploadAvatar(body: MultipartBody.Part): Result<ProfileMe> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.uploadAvatar(body)
                resultMapper.fromBaseResponse(response).map { it.toDomain() }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }

    override suspend fun getStatsOverview(): Result<LearningStatsOverview> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getStatsOverview()
                resultMapper.fromBaseResponse(response).map { it.toDomain() }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }

    override suspend fun getHeatmap(from: LocalDate?, to: LocalDate?): Result<List<HeatmapDay>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getHeatmap(
                    from?.format(isoDate),
                    to?.format(isoDate)
                )
                resultMapper.fromBaseResponse(response).map { list ->
                    list.mapNotNull { it.toDomain() }
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }

    override suspend fun getActivityDay(date: LocalDate): Result<ActivityDayDetail> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getActivityDay(date.format(isoDate))
                when (val r = resultMapper.fromBaseResponse(response)) {
                    is Result.Success -> {
                        val d = r.data.toActivityDayDetail()
                        if (d != null) Result.Success(d)
                        else Result.Error("Invalid day payload")
                    }
                    is Result.Error -> r
                    is Result.Loading -> Result.Error("Unexpected loading")
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }

    override fun completedExercisesPager(
        filter: String,
        query: String?
    ): Flow<PagingData<LearningActivityItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = LearningActivityHistoryPagingSource.PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 10
            ),
            pagingSourceFactory = { LearningActivityHistoryPagingSource(api, filter, query) }
        ).flow
    }

    override fun correctionsPager(filter: String, query: String?): Flow<PagingData<UserCorrectionItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = CorrectionsPagingSource.PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 10
            ),
            pagingSourceFactory = { CorrectionsPagingSource(api, filter, query) }
        ).flow
    }

    override suspend fun getCorrectionSession(sessionId: Int): Result<CorrectionSessionReview> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getCorrectionSession(sessionId)
                when (val r = resultMapper.fromBaseResponse(response)) {
                    is Result.Success -> {
                        val d = r.data.toDomain()
                        if (d != null) Result.Success(d)
                        else Result.Error("Invalid session payload")
                    }
                    is Result.Error -> r
                    else -> Result.Error("Unknown")
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }

    override suspend fun clearCorrections(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.clearCorrections()
            when (val r = resultMapper.fromBaseResponse(response)) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> r
                else -> Result.Error("Unknown")
            }
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }

    override fun vocabularyPager(filter: String, query: String?): Flow<PagingData<VocabularyWord>> {
        return Pager(
            config = PagingConfig(
                pageSize = VocabularyPagingSource.PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 10
            ),
            pagingSourceFactory = { VocabularyPagingSource(api, filter, query) }
        ).flow
    }

    override suspend fun patchVocabulary(
        id: Int,
        favorite: Boolean?,
        needsAttention: Boolean?
    ): Result<VocabularyWord> = withContext(Dispatchers.IO) {
        try {
            val response = api.patchVocabulary(
                id,
                UserLearnedWordPatchDto(favorite = favorite, needsAttention = needsAttention)
            )
            when (val r = resultMapper.fromBaseResponse(response)) {
                is Result.Success -> {
                    val w = r.data.toVocabularyWord()
                    if (w != null) Result.Success(w) else Result.Error("Invalid word")
                }
                is Result.Error -> r
                else -> Result.Error("Unknown")
            }
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }

    override suspend fun addVocabulary(term: String): Result<VocabularyWord> = withContext(Dispatchers.IO) {
        try {
            val response = api.addVocabulary(UserLearnedWordCreateDto(term = term.trim()))
            when (val r = resultMapper.fromBaseResponse(response)) {
                is Result.Success -> {
                    val w = r.data.toVocabularyWord()
                    if (w != null) Result.Success(w) else Result.Error("Invalid word")
                }
                is Result.Error -> r
                else -> Result.Error("Unknown")
            }
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }
}
