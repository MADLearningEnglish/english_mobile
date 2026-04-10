package com.mit.learning_english.domain.repository

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.profile.ActivityDayDetail
import com.mit.learning_english.domain.model.profile.CorrectionSessionReview
import com.mit.learning_english.domain.model.profile.HeatmapDay
import com.mit.learning_english.domain.model.profile.LearningActivityItem
import com.mit.learning_english.domain.model.profile.LearningStatsOverview
import com.mit.learning_english.domain.model.profile.ProfileMe
import com.mit.learning_english.domain.model.profile.UserCorrectionItem
import com.mit.learning_english.domain.model.profile.VocabularyWord
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import java.time.LocalDate

interface ProfileRepository {
    suspend fun getMe(): Result<ProfileMe>
    suspend fun patchMe(fullName: String?, location: String?, learningLevel: String?): Result<ProfileMe>
    suspend fun uploadAvatar(body: MultipartBody.Part): Result<ProfileMe>
    suspend fun getStatsOverview(): Result<LearningStatsOverview>
    suspend fun getHeatmap(from: LocalDate?, to: LocalDate?): Result<List<HeatmapDay>>
    suspend fun getActivityDay(date: LocalDate): Result<ActivityDayDetail>
    /** filter: ALL | LESSON | EXERCISE */
    fun completedExercisesPager(filter: String, query: String?): Flow<PagingData<LearningActivityItem>>
    /** filter: ALL | GRAMMAR | VOCABULARY | SPELLING */
    fun correctionsPager(filter: String, query: String?): Flow<PagingData<UserCorrectionItem>>
    suspend fun getCorrectionSession(sessionId: Int): Result<CorrectionSessionReview>
    suspend fun clearCorrections(): Result<Unit>
    fun vocabularyPager(filter: String, query: String?): Flow<PagingData<VocabularyWord>>
    suspend fun patchVocabulary(id: Int, favorite: Boolean?, needsAttention: Boolean?): Result<VocabularyWord>
    suspend fun addVocabulary(term: String): Result<VocabularyWord>
}
