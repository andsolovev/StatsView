package ru.netology.statsview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
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
            invalidate()
        }
    var maxData: Float = 0F

    private var radius = 0F
    private var center = PointF(0F, 0F)
    private var oval = RectF(0F, 0F, 0F, 0F)

    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()
    private var colorBack = 0

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
//        val sumValues = data.sum()
        var sumToDraw = 0F
        var startFrom = -90F
        var patchColor = 0
        var patchAngle = 0F

        paint.color = colorBack
        canvas.drawCircle(center.x, center.y, radius, paint)

        for ((index, value) in data.withIndex()) {
//            val arc = value / sumValues
            val arc = value / maxData
            sumToDraw += arc
            val angle = 360F * arc
            paint.color = colors.getOrNull(index) ?: randomColor()
            if(index == 0) {
                patchColor = paint.color
                patchAngle = angle
            }
            canvas.drawArc(oval, startFrom, angle, false, paint)
            startFrom += angle
        }

        if(startFrom == 270F) {
            paint.color = patchColor
            canvas.drawArc(oval, startFrom, patchAngle / 100, false, paint)
        }


        canvas.drawText(
            "%.2f%%".format(sumToDraw * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }

    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

}