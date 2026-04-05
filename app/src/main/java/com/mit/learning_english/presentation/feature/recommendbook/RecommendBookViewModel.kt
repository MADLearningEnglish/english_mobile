package com.mit.learning_english.presentation.feature.recommendbook

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.book.GetReadingInProgressUseCase
import com.mit.learning_english.domain.usecase.book.GetRecommendByAuthorUseCase
import com.mit.learning_english.domain.usecase.book.GetRecommendByTopicUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendBookViewModel @Inject constructor(
    private val getRecommendByTopicUseCase: GetRecommendByTopicUseCase,
    private val getRecommendByAuthorUseCase: GetRecommendByAuthorUseCase,
    private val getReadingInProgressUseCase: GetReadingInProgressUseCase
) : BaseViewModel<RecommendBookState, RecommendBookEvent>(RecommendBookState()) {

    init {
        load()
    }

    fun load() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            val topicDeferred = async { getRecommendByTopicUseCase() }
            val authorDeferred = async { getRecommendByAuthorUseCase() }
            val progressDeferred = async { getReadingInProgressUseCase() }

            val topicResult = topicDeferred.await()
            val authorResult = authorDeferred.await()
            val progressResult = progressDeferred.await()

            setState {
                copy(
                    booksByTopic = topicResult.getOrDefault(emptyList()),
                    booksByAuthor = authorResult.getOrDefault(emptyList()),
                    booksInProgress = progressResult.getOrDefault(emptyList())
                )
            }

            val allFailed = topicResult is Result.Error &&
                authorResult is Result.Error &&
                progressResult is Result.Error
            if (allFailed) {
                val msg = (topicResult as Result.Error).message
                emitError(msg)
            }

            setLoading(false)
        }
    }

    fun onBookClick(bookId: Int) {
        emitEvent(RecommendBookEvent.NavigateToBookDetail(bookId))
    }
}
