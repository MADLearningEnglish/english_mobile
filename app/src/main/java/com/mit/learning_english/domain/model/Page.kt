package com.mit.learning_english.domain.model

data class Page(
    val id: Int, val number: Int, val audio: Audio, val pages: List<Page>
)