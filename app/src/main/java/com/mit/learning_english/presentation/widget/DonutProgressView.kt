package com.mit.learning_english.presentation.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.mit.learning_english.R
import kotlin.math.min

/**
 * Vòng tròn tiến độ (donut) cho màn Profile — giố mockup "Language knowledge".
 */
class DonutProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val rect = RectF()

    var progress: Float = 0.5f
        set(value) {
            field = value.coerceIn(0f, 1f)
            invalidate()
        }

    private var strokeWidthPx: Float = 0f

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.DonutProgressView)
        try {
            strokeWidthPx = ta.getDimension(R.styleable.DonutProgressView_donutStrokeWidth, 24f * resources.displayMetrics.density)
        } finally {
            ta.recycle()
        }
        trackPaint.strokeWidth = strokeWidthPx
        progressPaint.strokeWidth = strokeWidthPx
        trackPaint.color = ContextCompat.getColor(context, R.color.profile_donut_track)
        progressPaint.color = ContextCompat.getColor(context, R.color.primary)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(w, h)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val inset = strokeWidthPx / 2f
        rect.set(inset, inset, width - inset, height - inset)
        canvas.drawArc(rect, -90f, 360f, false, trackPaint)
        // Bắt đầu từ 9h (180°) để vòng progress “đi lên” phía dưới giống mockup.
        canvas.drawArc(rect, 180f, 360f * progress, false, progressPaint)
    }
}
