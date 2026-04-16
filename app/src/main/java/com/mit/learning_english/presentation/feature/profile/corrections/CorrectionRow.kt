package com.mit.learning_english.presentation.feature.profile.corrections

import com.mit.learning_english.domain.model.profile.UserCorrectionItem

sealed class CorrectionRow {
    data class Header(val title: String) : CorrectionRow()
    data class EntryRow(val item: UserCorrectionItem) : CorrectionRow()
}
