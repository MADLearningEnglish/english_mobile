package com.mit.learning_english.domain.usecase

import com.mit.learning_english.domain.repository.BookRepository
import javax.inject.Inject

class UpdateFavoriteBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {}