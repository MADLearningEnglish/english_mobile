package com.mit.learning_english.presentation.feature.profile.edit

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.repository.ProfileRepository
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.shared.UiErrorKey
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

data class EditProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val avatarUrl: String? = null
)

sealed class EditProfileEvent {
    data object Saved : EditProfileEvent()
    data object NavigateBack : EditProfileEvent()
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    @ApplicationContext private val appContext: Context
) : BaseViewModel<EditProfileUiState, EditProfileEvent>(EditProfileUiState()) {

    init {
        viewModelScope.launch(exceptionHandler) {
            when (val r = profileRepository.getMe()) {
                is Result.Success -> {
                    val p = r.data
                    setState {
                        copy(
                            displayName = p.fullName.orEmpty(),
                            email = p.email,
                            avatarUrl = p.avatarUrl
                        )
                    }
                }
                is Result.Error -> emitError(r.message)
                else -> {}
            }
        }
    }

    fun save(displayName: String) {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            when (val r = profileRepository.patchMe(fullName = displayName, location = null, learningLevel = null)) {
                is Result.Success -> emitEvent(EditProfileEvent.Saved)
                is Result.Error -> emitError(r.message)
                else -> {}
            }
            setLoading(false)
        }
    }

    fun uploadAvatar(uri: Uri) {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            val cr = appContext.contentResolver
            val mime = cr.getType(uri) ?: "image/jpeg"
            val bytes = cr.openInputStream(uri)?.use { it.readBytes() } ?: run {
                emitError(UiErrorKey.CANNOT_READ_IMAGE)
                setLoading(false)
                return@launch
            }
            val body = bytes.toRequestBody(mime.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", "avatar.jpg", body)
            when (val r = profileRepository.uploadAvatar(part)) {
                is Result.Success -> {
                    setState { copy(avatarUrl = r.data.avatarUrl) }
                }
                is Result.Error -> emitError(r.message)
                else -> {}
            }
            setLoading(false)
        }
    }
}
