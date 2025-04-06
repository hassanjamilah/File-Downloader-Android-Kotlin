package com.hassan.filedownloader

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class ButtonState {
        Clicked, Loading, Completed
    }

    private var buttonText: String = "Download"
    private var buttonColor: Int = Color.BLUE
    private var textColor: Int = Color.WHITE

    private var buttonState: ButtonState = ButtonState.Completed
    private var progress: Float = 0f
    private var progressAnimator: ValueAnimator? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 50f
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            try {
                buttonText = getString(R.styleable.LoadingButton_buttonText) ?: "Download"
                buttonColor = getColor(R.styleable.LoadingButton_buttonColor, Color.BLUE)
                textColor = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw button background
        paint.color = buttonColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Draw button text
        paint.color = textColor
        val textX = width / 2f
        val textY = height / 2f - (paint.descent() + paint.ascent()) / 2f
        canvas.drawText(buttonText, textX, textY, paint)

        if (buttonState == ButtonState.Loading) {
            paint.color = Color.YELLOW
            val sweepAngle = 360 * progress
            val size = 60f
            val left = width - size - 40
            val top = (height - size) / 2
            val rect = RectF(left, top, left + size, top + size)
            canvas.drawArc(rect, 0f, sweepAngle, true, paint)
            paint.color = Color.DKGRAY
            val progressWidth = width * progress
            canvas.drawRect(0f, 0f, progressWidth, height.toFloat(), paint)
        }
    }

    fun startLoading() {
        buttonState = ButtonState.Loading
        buttonText = "Loading..."
        progress = 0f
        progressAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000L
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    fun completeLoading() {
        buttonState = ButtonState.Completed
        buttonText = "Download"
        progressAnimator?.cancel()
        progress = 0f
        invalidate()
    }

    private fun startProgressAnimation() {
        progressAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }
}