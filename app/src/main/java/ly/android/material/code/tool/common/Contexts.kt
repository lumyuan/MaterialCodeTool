package ly.android.material.code.tool.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import ly.android.io.common.IOUtils
import java.io.IOException
import kotlin.math.abs
import kotlin.math.pow

fun Context.width(): Int {
    val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val outPoint = Point()
    // 可能有虚拟按键的情况
    display.getRealSize(outPoint)
    return outPoint.x
}

fun Context.height(): Int {
    val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val outPoint = Point()
    // 可能有虚拟按键的情况
    display.getRealSize(outPoint)
    return outPoint.y
}

fun Context.px2dip(pxValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

fun Context.dip2px(dipValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}

fun Context.px2sp(pxValue: Float): Int {
    val fontScale = this.resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

fun Context.sp2px(spValue: Float): Int {
    val fontScale = this.resources.displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}

@Throws(IOException::class)
fun Context.readByte(fileName: String) : ByteArray {
    val open = assets.open(fileName)
    return IOUtils.readBytes(open)
}

fun Context.clip(content: CharSequence){
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, content))
}

fun Context.computeContrastBetweenColors(bg: Int, fg: Int): Float {
    var bgR = android.graphics.Color.red(bg) / 255f
    var bgG = android.graphics.Color.green(bg) / 255f
    var bgB = android.graphics.Color.blue(bg) / 255f
    bgR = if (bgR < 0.03928f) bgR / 12.92f else ((bgR + 0.055f) / 1.055f).toDouble().pow(2.4)
        .toFloat()
    bgG = if (bgG < 0.03928f) bgG / 12.92f else ((bgG + 0.055f) / 1.055f).toDouble().pow(2.4)
        .toFloat()
    bgB = if (bgB < 0.03928f) bgB / 12.92f else ((bgB + 0.055f) / 1.055f).toDouble().pow(2.4)
        .toFloat()
    val bgL = 0.2126f * bgR + 0.7152f * bgG + 0.0722f * bgB
    var fgR = android.graphics.Color.red(fg) / 255f
    var fgG = android.graphics.Color.green(fg) / 255f
    var fgB = android.graphics.Color.blue(fg) / 255f
    fgR = if (fgR < 0.03928f) fgR / 12.92f else ((fgR + 0.055f) / 1.055f).toDouble().pow(2.4)
        .toFloat()
    fgG = if (fgG < 0.03928f) fgG / 12.92f else ((fgG + 0.055f) / 1.055f).toDouble().pow(2.4)
        .toFloat()
    fgB = if (fgB < 0.03928f) fgB / 12.92f else ((fgB + 0.055f) / 1.055f).toDouble().pow(2.4)
        .toFloat()
    val fgL = 0.2126f * fgR + 0.7152f * fgG + 0.0722f * fgB
    return abs((fgL + 0.05f) / (bgL + 0.05f))
}