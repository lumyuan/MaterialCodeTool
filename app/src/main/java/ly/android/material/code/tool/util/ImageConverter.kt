package ly.android.material.code.tool.util

import android.graphics.Bitmap
import android.graphics.Canvas

import android.graphics.PixelFormat

import android.graphics.drawable.Drawable


object ImageConverter {

    fun drawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
        val bitmap: Bitmap?
        val config =
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        bitmap = Bitmap.createBitmap(width, height, config)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }


}