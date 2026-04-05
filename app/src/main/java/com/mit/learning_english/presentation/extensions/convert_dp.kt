package com.mit.learning_english.presentation.extensions

import android.content.Context
import android.util.TypedValue

fun Int.dp(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
    ).toInt()
}