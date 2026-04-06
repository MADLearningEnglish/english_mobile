package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LevelDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("numberCourse") val numberCourse: Int?,
    @SerializedName("status") val status: Int?
)
