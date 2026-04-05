package com.mit.learning_english.presentation.feature.ai

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.data.remote.dto.AiScenarioDto
import com.mit.learning_english.data.remote.dto.CreateChatSessionRequestDto
import com.mit.learning_english.data.remote.dto.CreateChatSessionResponseDto
import com.mit.learning_english.data.repository.AiChatRepository
import com.mit.learning_english.presentation.base.BaseViewModel
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
)

sealed class ChooseTopicEvent {
    data class OpenChat(
        val sessionId: Int,
        val title: String,
        val aiRole: String,
        val levelName: String,
        val instruction: String,
    ) : ChooseTopicEvent()
}

@HiltViewModel
class ChooseTopicViewModel @Inject constructor(
    private val repository: AiChatRepository,
) : BaseViewModel<ChooseTopicUiState, ChooseTopicEvent>(ChooseTopicUiState()) {

    init {
        loadScenarios(TopicLevelFilter.ALL)
    }

    fun onSearchQueryChange(query: String) {
        setState { copy(searchQuery = query) }
    }

    fun onFilterSelected(filter: TopicLevelFilter) {
        setState { copy(selectedFilter = filter) }
        loadScenarios(filter)
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
                repository.createSession(CreateChatSessionRequestDto(scenarioId = 0))
                    .onSuccess { res -> emitOpenChat(res, levelName = "FREE", scenario = null) }
                    .onFailure { onError(it) }
            } finally {
                setLoading(false)
            }
        }
    }

    fun startScenario(scenario: AiScenarioDto) {
        val sid = scenario.id ?: return
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            try {
                repository.createSession(CreateChatSessionRequestDto(scenarioId = sid))
                    .onSuccess { res -> emitOpenChat(res, scenario.levelName.orEmpty(), scenario) }
                    .onFailure { onError(it) }
            } finally {
                setLoading(false)
            }
        }
    }

    private fun emitOpenChat(
        res: CreateChatSessionResponseDto,
        levelName: String,
        scenario: AiScenarioDto?,
    ) {
        val sessionId = res.sessionId ?: run {
            emitError("Invalid session")
            return
        }
        val title = res.title ?: scenario?.title.orEmpty()
        val aiRole = res.aiRole ?: scenario?.aiRole.orEmpty()
        val instruction = res.instruction ?: scenario?.instruction.orEmpty()
        emitEvent(
            ChooseTopicEvent.OpenChat(
                sessionId = sessionId,
                title = title,
                aiRole = aiRole,
                levelName = levelName,
                instruction = instruction,
            ),
        )
    }
}
