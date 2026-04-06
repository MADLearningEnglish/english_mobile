package com.mit.learning_english.data.repository

import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.mapper.toLearningLevel
import com.mit.learning_english.data.remote.api.LevelApiService
import com.mit.learning_english.domain.model.LearningLevel
import com.mit.learning_english.domain.repository.LevelRepository
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LevelRepositoryImpl @Inject constructor(
    private val levelApiService: LevelApiService,
    private val resultMapper: ResultMapper
) : LevelRepository {
    override suspend fun getLevels(): Result<List<LearningLevel>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = levelApiService.getLevels()
                resultMapper.fromBaseResponse(response).map { listLevelDto ->
                    listLevelDto.map{it.toLearningLevel()}
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }
}
