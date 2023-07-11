package ru.netology.statsview.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.statsview.R
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes,
) {

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            update()
        }

    var maxData: Float = 0F
    var sumToDraw = 0F

    private var radius = 0F
    private var center = PointF(0F, 0F)
    private var oval = RectF(0F, 0F, 0F, 0F)

    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()
    private var colorBack = 0

    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null

    var animationType = 0

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
            colors = listOf(
                getColor(R.styleable.StatsView_color1, randomColor()),
                getColor(R.styleable.StatsView_color2, randomColor()),
                getColor(R.styleable.StatsView_color3, randomColor()),
                getColor(R.styleable.StatsView_color4, randomColor())
            )
            colorBack = getColor(R.styleable.StatsView_color_back, randomColor())
            animationType = getInteger(R.styleable.StatsView_animationType, 0)
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = this@StatsView.fontSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        paint.color = colorBack
        canvas.drawCircle(center.x, center.y, radius, paint)

        when(animationType) {
            0 -> fillSame(canvas)
            1 -> fillRotate(canvas)
            2 -> fillSequential(canvas)
            3 -> fillBidirectional(canvas)
        }

        canvas.drawText(
            "%.2f%%".format(sumToDraw * progress * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }

    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F

        valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 2_000
            interpolator = LinearInterpolator()
        }.also {
            it.start()
        }
    }

    private fun fillSame(canvas: Canvas) {

        sumToDraw = 0F
        var startFrom = -90F
        var patchColor = 0
        var patchAngle = 0F

        for ((index, value) in data.withIndex()) {
            val arc = value / maxData
            sumToDraw += arc
            val angle = 360F * arc
            paint.color = colors.getOrNull(index) ?: randomColor()
            if(index == 0) {
                patchColor = paint.color
                patchAngle = angle
            }
            canvas.drawArc(oval, startFrom, angle * progress, false, paint)
            startFrom += angle
        }

        if(startFrom == 270F) {
            paint.color = patchColor
            canvas.drawArc(oval, startFrom, patchAngle / 100, false, paint)
        }
    }

    private fun fillRotate(canvas: Canvas) {
        sumToDraw = 0F
        var startFrom = -90F + progress * 360
        var patchColor = 0
        var patchAngle = 0F

        for ((index, value) in data.withIndex()) {
            val arc = value / maxData
            sumToDraw += arc
            val angle = 360F * arc
            paint.color = colors.getOrNull(index) ?: randomColor()
            if(index == 0) {
                patchColor = paint.color
                patchAngle = angle
            }
            canvas.drawArc(oval, startFrom, angle * progress, false, paint)
            startFrom += angle
        }

        if(startFrom == 270F) {
            paint.color = patchColor
            canvas.drawArc(oval, startFrom, patchAngle / 100, false, paint)
        }
    }

    private fun fillSequential(canvas: Canvas) {
        sumToDraw = 0F
        var startFrom = -90F
        val maxAngle = startFrom + 360 * progress
        var patchColor = 0
        var patchAngle = 0F

        for ((index, value) in data.withIndex()) {
            if (startFrom > maxAngle) return
            val arc = value / maxData
            sumToDraw += arc
            val angle = arc * 360F
            val sweepTo = min(angle, maxAngle - startFrom)
            paint.color = colors.getOrNull(index) ?: randomColor()
            if(index == 0) {
                patchColor = paint.color
                patchAngle = angle
            }

            canvas.drawArc(oval, startFrom, sweepTo, false, paint)
            startFrom += angle
        }

        if(startFrom == 270F) {
            paint.color = patchColor
            canvas.drawArc(oval, startFrom, patchAngle / 100, false, paint)
        }
    }

    private fun fillBidirectional(canvas: Canvas) {

        sumToDraw = 0F
        var startFrom = -90F
        var patchColor = 0
        var patchAngle = 0F

        for ((index, value) in data.withIndex()) {
            val arc = value / maxData
            sumToDraw += arc
            val angle = 360F * arc
            paint.color = colors.getOrNull(index) ?: randomColor()
            if(index == 0) {
                patchColor = paint.color
                patchAngle = angle
            }
            canvas.drawArc(oval, startFrom + (angle / 2) - (angle * progress / 2), angle * progress, false, paint)
            startFrom += angle
        }

        if(startFrom == 270F) {
            paint.color = patchColor
            canvas.drawArc(oval, startFrom, patchAngle / 100, false, paint)
        }
    }

}