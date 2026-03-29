package com.mit.learning_english.presentation.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R

class MaxItemRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    private var maxVisibleItems: Int = DEFAULT_MAX_VISIBLE_ITEMS

    init {
        context.withStyledAttributes(attrs, R.styleable.MaxItemRecyclerView) {
            maxVisibleItems = getInt(
                R.styleable.MaxItemRecyclerView_maxVisibleItems, DEFAULT_MAX_VISIBLE_ITEMS
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val lm = layoutManager ?: return
        val itemCount = adapter?.itemCount ?: return

        if (itemCount > maxVisibleItems && isNotEmpty()) {
            var cappedHeight = paddingTop + paddingBottom
            val visibleCount = minOf(maxVisibleItems, childCount)
            for (i in 0 until visibleCount) {
                val child = getChildAt(i) ?: continue
                cappedHeight += lm.getDecoratedMeasuredHeight(child)
            }
            setMeasuredDimension(measuredWidth, cappedHeight)
        }
    }

    companion object {
        private const val DEFAULT_MAX_VISIBLE_ITEMS = 5
    }
}
