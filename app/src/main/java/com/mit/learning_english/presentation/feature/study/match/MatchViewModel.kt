package com.mit.learning_english.presentation.feature.study.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.deck.GetStudyFlashCardsUseCase
import com.mit.learning_english.domain.usecase.deck.LogDeckStudyCompleteUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getStudyFlashcardsUseCase: GetStudyFlashCardsUseCase,
    private val logDeckStudyCompleteUseCase: LogDeckStudyCompleteUseCase
) : BaseViewModel<MatchState, MatchEvent>(MatchState()) {

    private val deckId: Int = savedStateHandle.get<Int>("deckId") ?: 0
    private var sessionStartMs: Long? = null

    init {
        loadFlashcards()
    }

    private fun loadFlashcards() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            when (val result = getStudyFlashcardsUseCase(deckId)) {
                is Result.Success -> {
                    setLoading(false)
                    sessionStartMs = System.currentTimeMillis()
                    val flashcards = result.data
                    setState {
                        copy(
                            flashcards = flashcards,
                            totalRounds = 6,
                            currentRound = 1,
                            isPreGame = true
                        )
                    }
                    generateRoundCards()
                }
                is Result.Error -> {
                    setLoading(false)
                    emitError(result.message ?: "Lỗi tải dữ liệu")
                }
                else -> setLoading(false)
            }
        }
    }

    fun startGame() {
        setState { copy(isPreGame = false) }
    }

    private fun generateRoundCards() {
        val allFlashcards = uiState.value.flashcards
        if (allFlashcards.isEmpty()) return

        val roundFlashcards = allFlashcards.shuffled().take(4)
        
        val cards = mutableListOf<MatchCard>()
        roundFlashcards.forEach { fc ->
            cards.add(MatchCard(
                id = "term_${fc.id}",
                flashcardId = fc.id.toString(),
                text = fc.term,
                isTerm = true,
                imageUrl = fc.imageUrl
            ))
            cards.add(MatchCard(
                id = "def_${fc.id}",
                flashcardId = fc.id.toString(),
                text = fc.definition,
                isTerm = false,
                imageUrl = fc.imageUrl
            ))
        }

        setState {
            copy(
                cardsOnScreen = cards.shuffled(),
                selectedFirstCardId = null
            )
        }
    }

    fun onCardClicked(cardId: String) {
        val state = uiState.value
        val clickedCard = state.cardsOnScreen.find { it.id == cardId } ?: return

        if (clickedCard.state == CardState.CORRECT || clickedCard.state == CardState.HIDDEN) return
        if (state.selectedFirstCardId == cardId) return

        val currentCards = state.cardsOnScreen.toMutableList()
        val clickedIndex = currentCards.indexOfFirst { it.id == cardId }

        if (state.selectedFirstCardId == null) {
            currentCards[clickedIndex] = clickedCard.copy(state = CardState.SELECTED)
            setState { copy(cardsOnScreen = currentCards, selectedFirstCardId = cardId) }
        } else {
            val firstCard = currentCards.find { it.id == state.selectedFirstCardId } ?: return
            
            if (firstCard.flashcardId == clickedCard.flashcardId && firstCard.isTerm != clickedCard.isTerm) {
                val firstIndex = currentCards.indexOfFirst { it.id == firstCard.id }
                currentCards[firstIndex] = firstCard.copy(state = CardState.CORRECT)
                currentCards[clickedIndex] = clickedCard.copy(state = CardState.CORRECT)
                setState { copy(cardsOnScreen = currentCards, selectedFirstCardId = null) }
                
                viewModelScope.launch {
                    delay(500)
                    val currentLatestCards = uiState.value.cardsOnScreen.toMutableList()
                    val idx1 = currentLatestCards.indexOfFirst { it.id == firstCard.id }
                    if (idx1 != -1) currentLatestCards[idx1] = currentLatestCards[idx1].copy(state = CardState.HIDDEN)
                    
                    val idx2 = currentLatestCards.indexOfFirst { it.id == clickedCard.id }
                    if (idx2 != -1) currentLatestCards[idx2] = currentLatestCards[idx2].copy(state = CardState.HIDDEN)
                    
                    setState { copy(cardsOnScreen = currentLatestCards) }
                    checkRoundComplete()
                }
            } else {
                val firstIndex = currentCards.indexOfFirst { it.id == firstCard.id }
                currentCards[firstIndex] = firstCard.copy(state = CardState.INCORRECT)
                currentCards[clickedIndex] = clickedCard.copy(state = CardState.INCORRECT)
                setState { copy(cardsOnScreen = currentCards, selectedFirstCardId = null) }

                viewModelScope.launch {
                    delay(500)
                    val currentLatestCards = uiState.value.cardsOnScreen.toMutableList()
                    val resetFirstIndex = currentLatestCards.indexOfFirst { it.id == firstCard.id }
                    if (resetFirstIndex != -1) currentLatestCards[resetFirstIndex] = currentLatestCards[resetFirstIndex].copy(state = CardState.UNSELECTED)
                    
                    val resetClickedIndex = currentLatestCards.indexOfFirst { it.id == clickedCard.id }
                    if (resetClickedIndex != -1) currentLatestCards[resetClickedIndex] = currentLatestCards[resetClickedIndex].copy(state = CardState.UNSELECTED)
                    
                    setState { copy(cardsOnScreen = currentLatestCards) }
                }
            }
        }
    }

    private fun checkRoundComplete() {
        val state = uiState.value
        val isRoundDone = state.cardsOnScreen.all { it.state == CardState.HIDDEN }
        
        if (isRoundDone) {
            viewModelScope.launch {
                delay(300)
                val currentState = uiState.value
                if (currentState.currentRound >= currentState.totalRounds) {
                    setState { copy(isComplete = true) }
                    notifySessionComplete(currentState)
                } else {
                    setState { copy(currentRound = currentState.currentRound + 1) }
                    generateRoundCards()
                }
            }
        }
    }

    fun onNavigateBack() {
        emitEvent(MatchEvent.NavigateBack)
    }

    private fun notifySessionComplete(state: MatchState) {
        emitEvent(MatchEvent.SessionComplete)
        val start = sessionStartMs
        if (start == null || deckId <= 0) return
        val durationSec = ((System.currentTimeMillis() - start) / 1000).toInt().coerceAtLeast(1)
        viewModelScope.launch(exceptionHandler) {
            logDeckStudyCompleteUseCase(
                deckId = deckId,
                durationSeconds = durationSec,
                cardsReviewed = state.totalRounds * 4,
                quizCorrect = state.totalRounds * 4,
                quizTotal = state.totalRounds * 4
            )
        }
    }
}
