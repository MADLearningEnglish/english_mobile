package com.mit.learning_english.presentation.feature.chat

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.data.remote.dto.AiScenarioDto
import com.mit.learning_english.data.remote.dto.CreateChatSessionRequestDto
import com.mit.learning_english.data.repository.AiChatRepository
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.shared.UiErrorKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TopicLevelFilter {
    ALL,
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
}

data class ChooseTopicUiState(
    val rawScenarios: List<AiScenarioDto> = emptyList(),
    val selectedFilter: TopicLevelFilter = TopicLevelFilter.ALL,
    val searchQuery: String = "",
    /** Sau lần gọi API list scenarios gần nhất (để không hiện empty khi đang load). */
    val scenariosFetchCompleted: Boolean = false,
    val goalType: String = "COMMUNICATION",
    val focusSkill: String = "GRAMMAR",
    val coachingMode: String = "COACH",
)

sealed class ChooseTopicEvent {
    data class OpenChat(
        val sessionId: Int,
        val title: String,
        val aiRole: String,
        val levelName: String,
        val instruction: String,
        val goalType: String,
        val focusSkill: String,
        val coachingMode: String,
    ) : ChooseTopicEvent()
}

@HiltViewModel
class ChooseTopicViewModel @Inject constructor(
    private val repository: AiChatRepository,
) : BaseViewModel<ChooseTopicUiState, ChooseTopicEvent>(ChooseTopicUiState()) {

    private var searchJob: kotlinx.coroutines.Job? = null

    init {
        loadScenarios(TopicLevelFilter.ALL)
    }

    fun onSearchQueryChange(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(300)
            setState { copy(searchQuery = query) }
        }
    }

    fun onFilterSelected(filter: TopicLevelFilter) {
        setState { copy(selectedFilter = filter) }
        loadScenarios(filter)
    }

    fun onGoalSelected(goalType: String) {
        setState { copy(goalType = goalType) }
    }

    fun onFocusSelected(focusSkill: String) {
        setState { copy(focusSkill = focusSkill) }
    }

    fun onModeSelected(mode: String) {
        setState { copy(coachingMode = mode) }
    }

    private fun levelIdFor(filter: TopicLevelFilter): Int? = when (filter) {
        TopicLevelFilter.ALL -> null
        TopicLevelFilter.BEGINNER -> 1
        TopicLevelFilter.INTERMEDIATE -> 2
        TopicLevelFilter.ADVANCED -> 3
    }

    private fun loadScenarios(filter: TopicLevelFilter) {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            try {
                repository.listScenarios(levelId = levelIdFor(filter))
                    .onSuccess { list ->
                        setState {
                            copy(rawScenarios = list, scenariosFetchCompleted = true)
                        }
                    }
                    .onFailure {
                        setState { copy(rawScenarios = emptyList(), scenariosFetchCompleted = true) }
                        onError(it)
                    }
            } finally {
                setLoading(false)
            }
        }
    }

    fun startFreeTalk() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            try {
                emitOpenChat(levelName = "FREE", scenario = null)
            } finally {
                setLoading(false)
            }
        }
    }

    fun startScenario(scenario: AiScenarioDto) {
        scenario.id ?: return
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            try {
                emitOpenChat(scenario.levelName.orEmpty(), scenario)
            } finally {
                setLoading(false)
            }
        }
    }

    private fun emitOpenChat(
        levelName: String,
        scenario: AiScenarioDto?,
    ) {
        val state = uiState.value
        val req = CreateChatSessionRequestDto(
            scenarioId = if (scenario?.id == null) 0 else scenario.id,
            goalType = state.goalType,
            focusSkill = state.focusSkill,
            coachingMode = state.coachingMode,
            fluencyMode = state.coachingMode.equals("FLUENCY", ignoreCase = true),
        )
        viewModelScope.launch(exceptionHandler) {
            repository.createSession(req)
                .onSuccess { response ->
                    val sessionId = response.sessionId ?: run {
                        emitError(UiErrorKey.INVALID_SESSION)
                        return@onSuccess
                    }
                    val title = response.title ?: scenario?.title.orEmpty()
                    val aiRole = response.aiRole ?: scenario?.aiRole.orEmpty()
                    val instruction = response.instruction ?: scenario?.instruction.orEmpty()
                    emitEvent(
                        ChooseTopicEvent.OpenChat(
                            sessionId = sessionId,
                            title = title,
                            aiRole = aiRole,
                            levelName = levelName,
                            instruction = instruction,
                            goalType = response.goalType ?: state.goalType,
                            focusSkill = response.focusSkill ?: state.focusSkill,
                            coachingMode = response.coachingMode ?: state.coachingMode,
                        ),
                    )
                }
                .onFailure { onError(it) }
        }
    }
}
