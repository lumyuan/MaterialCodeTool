package ly.android.material.code.tool.ui.view

import android.animation.ObjectAnimator
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.DecelerateInterpolator

const val duration = 150L
const val onLongTime = 750L
const val onDownScale = 0.9f
const val onUpScale = 1f
private val interpolator = DecelerateInterpolator()

/**
 * @author https://github.com/lumyuan
 * @license Apache-2.0 license
 * @copyright 2023 lumyuan
 * View触感反馈扩展函数
 */
fun View.setOnFeedbackListener(
    clickable: Boolean = false/*是否开启点击波纹*/,
    callOnLongClick: Boolean = false/*是否响应长按事件*/,
    onLongClick: (View) -> Unit = {},
    click: (View) -> Unit = {}
) {
    var cancel = true
    val longTouchRunnable = Runnable {
        onLongClick(this)
        vibrationLong(this)
        cancel = false
        onUp(this)
    }
    if (clickable){
        isClickable = true
    }
    setOnTouchListener(object : OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if (cancel) {
                        performClick()
                        click(v)
                        vibrationUp(v)
                    }
                    onUp(v)
                    handler.removeCallbacks(longTouchRunnable)
                    return if (!clickable){
                        true
                    }else {
                        onTouchEvent(event)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = event.x
                    val y = event.y
                    println("x: $x\t\ty: $y")
                    if (x < 0 || y < 0 || x > v.measuredWidth || y > v.measuredHeight) {
                        onUp(v)
                        cancel = false
                        handler.removeCallbacks(longTouchRunnable)
                        return if (!clickable){
                            true
                        }else {
                            onTouchEvent(event)
                        }
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    onUp(v)
                    cancel = false
                    handler.removeCallbacks(longTouchRunnable)
                    return if (!clickable){
                        true
                    }else {
                        onTouchEvent(event)
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    cancel = true
                    onDown(v)
                    vibrationDown(v)
                    if (callOnLongClick){
                        handler.postDelayed(
                            longTouchRunnable, onLongTime
                        )
                    }
                }
            }
            return if (!clickable){
                true
            }else {
                onTouchEvent(event)
            }
        }
    })
}

private fun onDown(view: View) {
    val scaleX = ObjectAnimator.ofFloat(view, "scaleX", onDownScale)
    scaleX.duration = duration
    scaleX.interpolator = interpolator
    scaleX.start()

    val scaleY = ObjectAnimator.ofFloat(view, "scaleY", onDownScale)
    scaleY.duration = duration
    scaleY.interpolator = interpolator
    scaleY.start()
}

private fun onUp(view: View) {
    val scaleX = ObjectAnimator.ofFloat(view, "scaleX", onUpScale)
    scaleX.duration = duration
    scaleX.interpolator = interpolator
    scaleX.start()

    val scaleY = ObjectAnimator.ofFloat(view, "scaleY", onUpScale)
    scaleY.duration = duration
    scaleY.interpolator = interpolator
    scaleY.start()
}

private fun vibrationDown(view: View) {
    val flag =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.GESTURE_START else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) HapticFeedbackConstants.KEYBOARD_PRESS else HapticFeedbackConstants.VIRTUAL_KEY
    view.performHapticFeedback(flag)
}

private fun vibrationUp(view: View) {
    val flag =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.GESTURE_END else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) HapticFeedbackConstants.KEYBOARD_RELEASE else HapticFeedbackConstants.VIRTUAL_KEY
    view.performHapticFeedback(flag)
}

private fun vibrationLong(view: View) {
    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
}