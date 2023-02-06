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

    //垂直线
    private val lineVerticalBefore: Paint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = context.dip2px(1f).toFloat()
        color = Color.BLACK
    }
    private var lineVerticalAfter: Paint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = context.dip2px(1f).toFloat()
        color = Color.BLACK
    }

    //水平线
    private var lineHorizontalBefore: Paint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = context.dip2px(1f).toFloat()
        color = Color.BLACK
    }
    private var lineHorizontalAfter: Paint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = context.dip2px(1f).toFloat()
        color = Color.BLACK
    }

    private var textCard = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        this.color = inverse
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //竖线01
        val v1 = lineX - lineVerticalBefore.strokeWidth
        canvas.drawLine(
            v1, 0f, v1, height.toFloat(), lineVerticalBefore
        )
        //竖线02
        val v2 = lineX + context.dip2px(1f) + lineVerticalAfter.strokeWidth
        canvas.drawLine(
            v2, 0f, v2, height.toFloat(), lineVerticalAfter
        )

        //横线01
        val h1 = lineY - lineHorizontalBefore.strokeWidth
        canvas.drawLine(0f, h1, width.toFloat(), h1, lineHorizontalBefore)
        //横线02
        val h2 = lineY + context.dip2px(1f) + lineHorizontalAfter.strokeWidth
        canvas.drawLine(0f, h2, width.toFloat(), h2, lineHorizontalBefore)

        val cx = width / 2
        val cy = height / 2

        val textWith = textPaint.measureText(colorString)
        val padding = spacer * 2
        val cw = textWith + padding
        val ch = textPaint.textSize + padding

        if (lineX < cx && lineY < cy){
            //应右下 T
            rectF.left = v2 + spacer
            rectF.right = rectF.left + cw
            rectF.top = h2 + spacer
            rectF.bottom = rectF.top + ch
        }else if (lineX > cx && lineY < cy){
            //应左下 T
            rectF.left = v1 - (spacer + cw)
            rectF.right = rectF.left + cw
            rectF.top = h2 + spacer
            rectF.bottom = rectF.top + ch
        }else if (lineX < cx && lineY > cy){
            //应右上 T
            rectF.left = v2 + spacer
            rectF.right = rectF.left + cw
            rectF.top = h1 - (spacer + ch)
            rectF.bottom = rectF.top + ch
        }else {
            rectF.left = v1 - (spacer + cw)
            rectF.right = rectF.left + cw
            rectF.top = h1 - (spacer + ch)
            rectF.bottom = rectF.top + ch
        }
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

        lineVerticalBefore.color = inverse
        lineVerticalAfter.color = inverse
        lineHorizontalBefore.color = inverse
        lineHorizontalAfter.color = inverse

        textPaint.color = if (isNotGone){
            colorNumber
        }else {
            Color.WHITE
        }
        textCard.color = inverse
    }
}