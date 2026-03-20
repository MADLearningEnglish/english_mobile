package com.mit.learning_english.presentation.feature.readbook

import com.mit.learning_english.presentation.base.BaseViewModel

class ReadBookViewModel : BaseViewModel<ReadBookState, ReadBookEvent>(ReadBookState()) {
    fun handle(a: Int = 0, b: Int?) {
        println(a)
        print(b)
    }

    fun a() {
        handle(2)
    }
}