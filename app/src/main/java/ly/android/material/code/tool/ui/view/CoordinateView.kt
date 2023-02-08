package ly.android.material.code.tool.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ly.android.material.code.tool.common.computeContrastBetweenColors
import ly.android.material.code.tool.common.dip2px
import ly.android.material.code.tool.common.sp2px


/**
 * 图片取色器--坐标
 */
class CoordinateView: View {

    private var isDrag = false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var lineX = 0f
    private var lineY = 0f

    private var ox = 0f
    private var oy = 0f

    private var downX = 0f
    private var downY = 0f

    private var xDistance = 0f
    private var yDistance = 0f

    private var onCoordinateChange: (x: Float, y: Float) -> Unit = { _, _ -> }
    private var onFocusChange: (hasFocus: Boolean) -> Unit = {_ -> }

    private var colorString: String = "#00000000"
    private var inverse = Color.WHITE
    private var borderColor = Color.BLACK

    private val borderWidth = context.dip2px(2f).toFloat()

    private var textCard = Paint().apply {
        style = Paint.Style.FILL
        this.color = inverse
    }

    private var border = Paint().apply {
        style = Paint.Style.STROKE
        this.color = borderColor
        this.strokeWidth = borderWidth
    }

    private val rectF = RectF()

    private var textPaint = TextPaint().apply {
        this.color = if (inverse == Color.WHITE){
            Color.BLACK
        }else {
            Color.WHITE
        }
        this.textSize = context.sp2px(16f).toFloat()
        this.textAlign = Paint.Align.CENTER
    }

    private val radius = context.dip2px(12f).toFloat()
    private val spacer = context.dip2px(10f).toFloat()

    private val circleRectF = RectF()
    private val circleRadius = context.dip2px(2f).toFloat()
    private val circleStrokeWidth = context.dip2px(1f).toFloat()
    private var pointCenter = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = circleStrokeWidth
    }

    private var line = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = circleStrokeWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2
        val cy = height / 2

        val textWith = textPaint.measureText(colorString)
        val padding = spacer * 2
        val cw = textWith + padding
        val ch = textPaint.textSize + padding

        if (lineX < cx && lineY < cy){
            //应右下 T
            rectF.left = lineX + spacer
            rectF.right = rectF.left + cw
            rectF.top = lineY + spacer
            rectF.bottom = rectF.top + ch
        }else if (lineX > cx && lineY < cy){
            //应左下 T
            rectF.left = lineX - (spacer + cw)
            rectF.right = rectF.left + cw
            rectF.top = lineY + spacer
            rectF.bottom = rectF.top + ch
        }else if (lineX < cx && lineY > cy){
            //应右上 T
            rectF.left = lineX + spacer
            rectF.right = rectF.left + cw
            rectF.top = lineY - (spacer + ch)
            rectF.bottom = rectF.top + ch
        }else {
            rectF.left = lineX - (spacer + cw)
            rectF.right = rectF.left + cw
            rectF.top = lineY - (spacer + ch)
            rectF.bottom = rectF.top + ch
        }

        //准星中心点
        circleRectF.apply {
            left = lineX - circleRadius
            right = lineX + circleRadius
            top = lineY - circleRadius
            bottom = lineY + circleRadius
        }
        canvas.drawRoundRect(circleRectF, circleRadius, circleRadius, pointCenter)

        //准星线
        canvas.drawLine(0f, lineY, circleRectF.left, lineY, line)
        canvas.drawLine(circleRectF.right, lineY, width.toFloat(), lineY, line)
        canvas.drawLine(lineX, 0f, lineX, circleRectF.top, line)
        canvas.drawLine(lineX, circleRectF.bottom, lineX, height.toFloat(), line)

        //边框
        canvas.drawRoundRect(rectF, radius, radius, border)
        //卡片
        canvas.drawRoundRect(rectF, radius, radius, textCard)

        val fontMetrics = textPaint.fontMetrics
        val baseline = (rectF.bottom + rectF.top - fontMetrics.bottom - fontMetrics.top) / 2

        canvas.drawText(colorString.uppercase(), rectF.centerX(), baseline, textPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                invalidate()
                onFocusChange(true)
            }

            MotionEvent.ACTION_MOVE -> {
                xDistance = event.x - downX
                yDistance = event.y - downY

                lineX = ox + xDistance
                lineY = oy + yDistance

                if (lineX < 0 || lineX > width){
                    xDistance = 0f
                    lineX = if (lineX < 0){
                        0f
                    }else {
                        width.toFloat()
                    }
                    ox = lineX
                }

                if (lineY < 0 || lineY > height){
                    yDistance = 0f
                    lineY = if (lineY < 0){
                        0f
                    }else {
                        height.toFloat()
                    }
                    oy = lineY
                }

                onCoordinateChange(lineX, lineY)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                ox += xDistance
                oy += yDistance
                onFocusChange(false)
            }
        }
        return !isDrag
    }

    fun setOnCoordinateChangeListener(onCoordinateChangeListener: (x: Float, y: Float) -> Unit){
        this.onCoordinateChange = onCoordinateChangeListener
    }

    fun setOnFocusChangeListener(onFocusChangeListener: (hasFocus: Boolean) -> Unit){
        this.onFocusChange = onFocusChangeListener
    }

    fun isDrag(): Boolean = isDrag

    fun setCanDrag(isDrag: Boolean){
        this.isDrag = isDrag
    }

    fun setPointColor(color: String){
        println(color)
        colorString = color
        val colorNumber = Color.parseColor(color)

        val isNotGone = Color.alpha(colorNumber) >= 127.5f
        inverse = if (context.computeContrastBetweenColors(colorNumber, Color.WHITE) > 3f && isNotGone){
            Color.WHITE
        }else {
            Color.BLACK
        }

        pointCenter.color = inverse
        line.color = inverse

        textPaint.color = if (isNotGone){
            colorNumber
        }else {
            Color.WHITE
        }
        textCard.color = inverse
        border.color = if (inverse == Color.WHITE){
            Color.BLACK
        }else {
            Color.WHITE
        }
    }
}