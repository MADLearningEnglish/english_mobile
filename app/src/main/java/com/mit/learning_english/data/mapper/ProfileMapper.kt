package com.mit.learning_english.data.mapper

import com.mit.learning_english.data.remote.dto.ActivityDayDetailDto
import com.mit.learning_english.data.remote.dto.HeatmapDayDto
import com.mit.learning_english.data.remote.dto.LearningActivityItemDto
import com.mit.learning_english.data.remote.dto.LearningStatsOverviewDto
import com.mit.learning_english.data.remote.dto.ProfileMeDto
import com.mit.learning_english.data.remote.dto.UserLearnedWordDto
import com.mit.learning_english.domain.model.profile.ActivityDayDetail
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
    detailJson = detailJson
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
