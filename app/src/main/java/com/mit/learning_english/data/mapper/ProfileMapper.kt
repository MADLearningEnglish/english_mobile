package com.mit.learning_english.data.mapper

import com.mit.learning_english.data.remote.dto.ActivityDayDetailDto
import com.mit.learning_english.data.remote.dto.HeatmapDayDto
import com.mit.learning_english.data.remote.dto.LearningActivityItemDto
import com.mit.learning_english.data.remote.dto.LearningStatsOverviewDto
import com.mit.learning_english.data.remote.dto.ProfileMeDto
import com.mit.learning_english.data.remote.dto.UserLearnedWordDto
import com.mit.learning_english.data.remote.dto.CorrectionSessionReviewDto
import com.mit.learning_english.data.remote.dto.SessionCorrectionDetailDto
import com.mit.learning_english.data.remote.dto.UserCorrectionItemDto
import com.mit.learning_english.domain.model.profile.ActivityDayDetail
import com.mit.learning_english.domain.model.profile.CorrectionSessionReview
import com.mit.learning_english.domain.model.profile.SessionCorrectionDetail
import com.mit.learning_english.domain.model.profile.UserCorrectionItem
import com.mit.learning_english.domain.model.profile.HeatmapDay
import com.mit.learning_english.domain.model.profile.LearningActivityItem
import com.mit.learning_english.domain.model.profile.LearningStatsOverview
import com.mit.learning_english.domain.model.profile.ProfileMe
import com.mit.learning_english.domain.model.profile.VocabularyWord
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val isoDate: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

fun ProfileMeDto.toDomain(): ProfileMe = ProfileMe(
    userId = userId ?: 0,
    email = email.orEmpty(),
    fullName = fullName,
    avatarUrl = avatarUrl,
    location = location,
    learningLevel = learningLevel,
    levelName = levelName,
    dailyGoalMinutes = dailyGoalMinutes,
    learningGoal = learningGoal,
    jobTitle = jobTitle
)

fun LearningStatsOverviewDto.toDomain(): LearningStatsOverview = LearningStatsOverview(
    completedLessonsOrExercises = completedLessonsOrExercises,
    wordsLearnedCount = wordsLearnedCount,
    currentStreakDays = currentStreakDays,
    totalStudyDays = totalStudyDays
)

fun HeatmapDayDto.toDomain(): HeatmapDay? {
    val d = date ?: return null
    return try {
        HeatmapDay(
            date = LocalDate.parse(d, isoDate),
            intensity = intensity,
            totalMinutes = totalMinutes,
            activityCount = activityCount
        )
    } catch (_: Exception) {
        null
    }
}

fun LearningActivityItemDto.toDomain(): LearningActivityItem = LearningActivityItem(
    id = id,
    activityType = activityType,
    title = title,
    durationSeconds = durationSeconds,
    scorePercent = scorePercent,
    wordsNewCount = wordsNewCount,
    detailJson = detailJson,
    referenceType = referenceType,
    referenceId = referenceId,
    startedAt = startedAt
)

fun ActivityDayDetailDto.toActivityDayDetail(): ActivityDayDetail? {
    val d = date ?: return null
    return try {
        ActivityDayDetail(
            date = LocalDate.parse(d, isoDate),
            totalMinutes = totalMinutes,
            activityCount = activityCount,
            averageScorePercent = averageScorePercent,
            dailyInsight = dailyInsight,
            activities = activities?.map { it.toDomain() }.orEmpty()
        )
    } catch (_: Exception) {
        null
    }
}

fun UserCorrectionItemDto.toUserCorrectionItem(): UserCorrectionItem? {
    val eid = errorId ?: return null
    val mid = messageId ?: return null
    val sid = sessionId ?: return null
    return UserCorrectionItem(
        errorId = eid,
        errorType = errorType,
        originalText = originalText,
        suggestedText = suggestedText,
        explanation = explanation,
        occurredAt = occurredAt,
        messageId = mid,
        sessionId = sid,
        sessionTitle = sessionTitle,
        sourceLabel = sourceLabel
    )
}

fun SessionCorrectionDetailDto.toDetail(): SessionCorrectionDetail? {
    val eid = errorId ?: return null
    return SessionCorrectionDetail(
        errorId = eid,
        category = category,
        originalText = originalText,
        suggestedText = suggestedText,
        explanation = explanation
    )
}

fun CorrectionSessionReviewDto.toDomain(): CorrectionSessionReview? {
    val sid = sessionId ?: return null
    return CorrectionSessionReview(
        sessionId = sid,
        contextHeader = contextHeader,
        sessionTitle = sessionTitle,
        sessionStartedAt = sessionStartedAt,
        sessionEndedAt = sessionEndedAt,
        durationMinutes = durationMinutes,
        improvementCount = improvementCount ?: 0,
        improvements = improvements?.mapNotNull { it.toDetail() }.orEmpty()
    )
}

fun UserLearnedWordDto.toVocabularyWord(): VocabularyWord? {
    val wid = id ?: return null
    return VocabularyWord(
        id = wid,
        term = term.orEmpty(),
        phonetic = phonetic,
        definition = definition,
        favorite = favorite == true,
        needsAttention = needsAttention == true,
        audioUrl = audioUrl
    )
}
