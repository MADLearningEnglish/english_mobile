package com.mit.learning_english.domain.usecase.dictionary

import com.mit.learning_english.data.remote.api.DictionaryApiService
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class FetchPhoneticUseCase @Inject constructor(
    private val dictionaryApiService: DictionaryApiService
) {
    suspend operator fun invoke(word: String): Result<String> {
        return try {
            if (word.isBlank()) {
                return Result.Error("Từ vựng trống")
            }
            val response = dictionaryApiService.getWordInfo(word.trim())
            if (response.isSuccessful && response.body() != null) {
                val entries = response.body()!!
                // Tìm text từ phần tử đầu tiên có phiên âm hợp lệ
                val phoneticText = entries.firstOrNull()?.phonetics?.firstOrNull { 
                    !it.text.isNullOrBlank() && it.text.startsWith("/") 
                }?.text

                if (phoneticText != null) {
                    Result.Success(phoneticText)
                } else {
                    Result.Error("Không tìm thấy phiên âm")
                }
            } else {
                Result.Error("Không tìm thấy từ vựng")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Lỗi kết nối từ điển")
        }
    }
}
