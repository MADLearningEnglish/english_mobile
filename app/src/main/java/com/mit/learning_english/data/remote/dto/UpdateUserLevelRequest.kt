package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateUserLevelRequest(
    @SerializedName("levelId") val levelId: Int
)
