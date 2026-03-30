package com.mit.learning_english.data.repository

import android.util.Log
import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.mapper.toPage
import com.mit.learning_english.data.remote.api.PageApiService
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.repository.PageRepository
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PageRepositoryImpl @Inject constructor(
    private val pageApi: PageApiService, private val resultMapper: ResultMapper
) : PageRepository {
    override suspend fun getPagesByChapter(
        chapterId: Int, pageNumbers: List<Int>
    ): Result<List<Page>> {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    pageApi.getPagesByChapter(chapterId = chapterId, pageNumbers = pageNumbers)
                val result = resultMapper.fromBaseResponse(response)
                    .map { pageResponses -> pageResponses.map { it.toPage() } }
                result
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }

    override suspend fun getPagesByBook(
        bookId: Int, pageNumbers: List<Int>
    ): Result<List<Page>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = pageApi.getPagesByBook(bookId = bookId, pageNumbers = pageNumbers)
                val result = resultMapper.fromBaseResponse(response)
                    .map { pageResponses -> pageResponses.map { it.toPage() } }
                Log.d("getPagesBYBook",result.toString())
                result
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }
}