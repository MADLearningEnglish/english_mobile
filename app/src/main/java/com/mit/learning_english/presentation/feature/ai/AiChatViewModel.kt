package com.mit.learning_english.presentation.feature.ai

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.data.remote.dto.CreateChatSessionRequestDto
import com.mit.learning_english.data.remote.dto.EndSessionResponseDto
import com.mit.learning_english.data.repository.AiChatRepository
import com.mit.learning_english.domain.repository.ProfileRepository
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.presentation.feature.ai.model.ChatListItem
import com.mit.learning_english.presentation.feature.ai.model.appendSendResponseItems
import com.mit.learning_english.presentation.feature.ai.model.mapTranscriptToChatItems
import com.mit.learning_english.presentation.feature.ai.model.toSessionSummaryArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class AiChatUiState(
    val items: List<ChatListItem> = emptyList(),
    val bottomHint: String? = null,
    val transcriptLoaded: Boolean = false,
    val isRecording: Boolean = false,
    val recordElapsedSec: Int = 0,
)

sealed class AiChatEvent {
    data class ShowSummary(val summary: EndSessionResponseDto) : AiChatEvent()
    data class ShowToast(val message: String) : AiChatEvent()
    data class OpenChat(
        val sessionId: Int,
        val title: String,
        val aiRole: String,
        val levelName: String,
        val instruction: String,
        val goalType: String,
        val focusSkill: String,
        val coachingMode: String,
    ) : AiChatEvent()
}

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val repository: AiChatRepository,
    private val profileRepository: ProfileRepository,
) : BaseViewModel<AiChatUiState, AiChatEvent>(AiChatUiState()) {

    private var sessionId: Int = 0
    private var scenarioInstruction: String = ""

    private var mediaRecorder: MediaRecorder? = null
    private var recordFile: File? = null
    private var tickJob: Job? = null

    fun initSession(sessionId: Int, instruction: String) {
        if (this.sessionId != 0) return
        this.sessionId = sessionId
        this.scenarioInstruction = instruction
        loadTranscript()
    }

    private fun loadTranscript() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            repository.transcript(sessionId)
                .onSuccess { list ->
                    val hint = list
                        .asReversed()
                        .firstOrNull { it.senderType?.equals("USER", true) == true }
                        ?.feedback
                        ?.let { fb ->
                            fb.feedbackLayers?.layer1Tip?.takeIf { it.isNotBlank() }
                                ?: fb.overallComment?.takeIf { it.isNotBlank() }
                                ?: fb.naturalSuggestion?.takeIf { it.isNotBlank() }
                        }
                    setState {
                        copy(
                            items = mapTranscriptToChatItems(scenarioInstruction, list),
                            bottomHint = hint,
                            transcriptLoaded = true,
                        )
                    }
                }
                .onFailure {
                    setState { copy(transcriptLoaded = true) }
                    onError(it)
                }
            setLoading(false)
        }
    }

    fun sendTextMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            repository.sendText(sessionId, trimmed)
                .onSuccess { res ->
                    val feedback = res.feedback
                    val hint = feedback?.feedbackLayers?.layer1Tip?.takeIf { it.isNotBlank() }
                        ?: feedback?.overallComment?.takeIf { it.isNotBlank() }
                        ?: feedback?.naturalSuggestion?.takeIf { it.isNotBlank() }
                    val appended = appendSendResponseItems(
                        feedback,
                        res.aiMessage?.content.orEmpty(),
                        res.userMessage?.inputType,
                    )
                    setState {
                        copy(
                            items = items + ChatListItem.User(trimmed) + appended,
                            bottomHint = hint ?: bottomHint,
                        )
                    }
                }
                .onFailure { onError(it) }
            setLoading(false)
        }
    }

    fun startRecording(context: Context) {
        if (uiState.value.isRecording) return
        val file = File(context.cacheDir, "ai_voice_${System.currentTimeMillis()}.m4a")
        recordFile = file
        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setOutputFile(file.absolutePath)
            recorder.prepare()
            recorder.start()
            mediaRecorder = recorder
            setState { copy(isRecording = true, recordElapsedSec = 0) }
            tickJob?.cancel()
            tickJob = viewModelScope.launch {
                val startTime = android.os.SystemClock.elapsedRealtime()
                while (isActive) {
                    delay(250)
                    val currentSec = ((android.os.SystemClock.elapsedRealtime() - startTime) / 1000).toInt()
                    setState { copy(recordElapsedSec = currentSec) }
                }
            }
        } catch (e: Exception) {
            recorder.release()
            mediaRecorder = null
            recordFile = null
            onError(e)
        }
    }

    fun cancelRecording() {
        tickJob?.cancel()
        tickJob = null
        runCatching {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        }
        mediaRecorder = null
        recordFile?.delete()
        recordFile = null
        setState { copy(isRecording = false, recordElapsedSec = 0) }
    }

    fun stopRecordingAndSend() {
        val file = recordFile ?: run {
            cancelRecording()
            return
        }
        val elapsed = uiState.value.recordElapsedSec
        tickJob?.cancel()
        tickJob = null
        runCatching {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        }
        mediaRecorder = null
        recordFile = null
        setState { copy(isRecording = false, recordElapsedSec = 0) }

        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            repository.sendVoice(sessionId, file, elapsed)
                .onSuccess { res ->
                    val feedback = res.feedback
                    val userText = res.userMessage?.content.orEmpty()
                    val hint = feedback?.feedbackLayers?.layer1Tip?.takeIf { it.isNotBlank() }
                        ?: feedback?.overallComment?.takeIf { it.isNotBlank() }
                        ?: feedback?.naturalSuggestion?.takeIf { it.isNotBlank() }
                    val appended = appendSendResponseItems(
                        feedback,
                        res.aiMessage?.content.orEmpty(),
                        res.userMessage?.inputType,
                    )
                    setState {
                        copy(
                            items = items + ChatListItem.User(userText.ifBlank { "…" }) + appended,
                            bottomHint = hint ?: bottomHint,
                        )
                    }
                    file.delete()
                }
                .onFailure {
                    file.delete()
                    onError(it)
                }
            setLoading(false)
        }
    }

    fun endSession() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            repository.endSession(sessionId)
                .onSuccess { dto ->
                    emitEvent(AiChatEvent.ShowSummary(dto))
                }
                .onFailure { onError(it) }
            setLoading(false)
        }
    }

    fun startNextTopicSession(
        topic: String,
        levelName: String,
        goalType: String,
        focusSkill: String,
        coachingMode: String,
        aiRole: String,
    ) {
        val normalizedTopic = topic.trim()
        if (normalizedTopic.isEmpty()) return
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            val req = CreateChatSessionRequestDto(
                scenarioId = 0,
                goalType = goalType,
                focusSkill = focusSkill,
                coachingMode = coachingMode,
                fluencyMode = coachingMode.equals("FLUENCY", ignoreCase = true),
            )
            repository.createSession(req)
                .onSuccess { response ->
                    val newSessionId = response.sessionId ?: run {
                        emitEvent(AiChatEvent.ShowToast("Không tạo được session mới"))
                        return@onSuccess
                    }
                    emitEvent(
                        AiChatEvent.OpenChat(
                            sessionId = newSessionId,
                            title = normalizedTopic,
                            aiRole = response.aiRole ?: aiRole,
                            levelName = levelName,
                            instruction = normalizedTopic,
                            goalType = response.goalType ?: goalType,
                            focusSkill = response.focusSkill ?: focusSkill,
                            coachingMode = response.coachingMode ?: coachingMode,
                        ),
                    )
                }
                .onFailure { onError(it) }
            setLoading(false)
        }
    }

    fun saveSelectedTextToVocabulary(text: String) {
        val term = text.trim()
        if (term.isEmpty()) return
        viewModelScope.launch(exceptionHandler) {
            when (profileRepository.addVocabulary(term)) {
                is Result.Success -> emitEvent(AiChatEvent.ShowToast("Saved to vocabulary"))
                is Result.Error -> emitEvent(AiChatEvent.ShowToast("Could not save word"))
                else -> Unit
            }
        }
    }

    override fun onCleared() {
        cancelRecording()
        super.onCleared()
    }
}
