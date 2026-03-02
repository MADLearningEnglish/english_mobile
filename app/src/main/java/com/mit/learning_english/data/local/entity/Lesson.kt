package com.mit.learning_english.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Lesson(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "lesson_name") val lessonName: String?,
    @ColumnInfo(name = "level") val level: Int?
)