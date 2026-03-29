package com.mit.learning_english.domain.repository

import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.util.Result

interface PageRepository {
    suspend fun getPagesByChapter(chapterId: Int, pageNumbers: List<Int>): Result<List<Page>>
    suspend fun getPagesByBook(bookId: Int, pageNumbers: List<Int>): Result<List<Page>>
}