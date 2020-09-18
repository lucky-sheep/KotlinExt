package com.xueyu.kotlinextlibrary

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import android.media.ExifInterface
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.xueyu.kotlinextlibrary.core.app
import java.io.*

/**
 * 添加文字水印
 *
 * @param content  文字内容
 * @param textSize 文字大小
 * @param color    文字颜色
 * @param x        文字起始x
 * @param y        文字起始y
 * @return the bitmap with text watermarking
 */
fun Bitmap.addTextWatermark(
    content: String?,
    textSize: Float,
    @ColorInt color: Int,
    x: Float,
    y: Float
): Bitmap? {
    val src = this
    if (isEmptyBitmap(src) || content == null) return null
    val ret = src.copy(src.config, true)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val canvas = Canvas(ret)
    paint.color = color
    paint.textSize = textSize
    val bounds = Rect()
    paint.getTextBounds(content, 0, content.length, bounds)
    canvas.drawText(content, x, y + textSize, paint)
    return ret
}

/**
 * 添加图片水印
 *
 * @param watermark watermark
 * @param x         起始x
 * @param y         文字起始y
 * @param alpha     水印透明度
 * @return the bitmap with image watermarking
 */
fun Bitmap.addImageWatermark(
    watermark: Bitmap,
    x: Int,
    y: Int,
    alpha: Int
): Bitmap? {
    val src = this
    if (isEmptyBitmap(src)) return null
    val ret = src.copy(src.config, true)
    if (!isEmptyBitmap(watermark)) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val canvas = Canvas(ret)
        paint.alpha = alpha
        canvas.drawBitmap(watermark, x.toFloat(), y.toFloat(), paint)
    }
    return ret
}

/**
 * drawable 转 bitmap
 *
 * @return Bitmap?
 */
fun Drawable.toBitmap(): Bitmap {
    val drawable = this
    if (drawable is BitmapDrawable) {
        if (drawable.bitmap != null) {
            return drawable.bitmap
        }
    }
    val bitmap: Bitmap
    if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        bitmap = Bitmap.createBitmap(
            1, 1,
            if (drawable.opacity != PixelFormat.OPAQUE)
                Bitmap.Config.ARGB_8888
            else
                Bitmap.Config.RGB_565
        )
    } else {
        bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            if (drawable.opacity != PixelFormat.OPAQUE)
                Bitmap.Config.ARGB_8888
            else
                Bitmap.Config.RGB_565
        )
    }
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

/**
 * Bitmap 转 Drawable
 *
 * @return Drawable
 */
fun Bitmap.toDrawable(): Drawable {
    return BitmapDrawable(app.resources, this)
}

/**
 * Bitmap 转 bytes.
 *
 * @param format bitmap的类型 支持PNG,JPEG,WEBP. 默认PNG
 * @return bytes
 */
fun Bitmap.toBytes(format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): ByteArray {
    val bitmap = this
    val baos = ByteArrayOutputStream()
    bitmap.compress(format, 100, baos)
    return baos.toByteArray()
}

/**
 * Bytes 转 bitmap.
 *
 * @return bitmap
 */
fun ByteArray?.toBitmap(): Bitmap? {
    val bytes = this
    return if (bytes == null || bytes.isEmpty())
        null
    else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

/**
 * Drawable 转 bytes.
 *
 * @param format bitmap的类型 支持PNG,JPEG,WEBP. 默认PNG
 * @return bytes
 */
fun Drawable.toByte(format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): ByteArray {
    val drawable = this
    return drawable.toBitmap().toBytes(format)
}

/**
 * Bytes 转 drawable.
 *
 * @return drawable
 */
fun ByteArray.toDrawable(): Drawable? {
    val bytes = this
    return bytes.toBitmap()?.toDrawable()
}

/**
 * view 转 bitmap
 * 注意，列表级控件无法直接转换
 *
 * @return Bitmap
 */
fun View.toBitmap(): Bitmap {
    val view = this
    view.isDrawingCacheEnabled = true
    view.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    view.layout(
        0, 0, view.measuredWidth, view.measuredHeight
    )
    return view.drawingCache
}

/**
 * 根据路径获取image大小
 * 注意 获取时需要采用转换过的路径
 *
 * @return Point
 */
val String.imageSize: Point
    get() {
        val path = this
        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        decodeFile(path, opts)
        val degree = getOrientation(path)
        return if (degree % 180 != 0) {
            Point(opts.outHeight, opts.outWidth)
        } else Point(opts.outWidth, opts.outHeight)
    }

private fun getOrientation(path: String): Int {
    if (TextUtils.isEmpty(path) || !path.endsWith(".jpg")) {
        return 0
    }
    try {
        val exifInterface = ExifInterface(path)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> return 90
            ExifInterface.ORIENTATION_ROTATE_180 -> return 180
            ExifInterface.ORIENTATION_ROTATE_270 -> return 270
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return 0
}

private fun decodeFile(path: String, opts: BitmapFactory.Options? = null): Bitmap? {
    var bitmap: Bitmap? =
        null
    var stream: InputStream? = null
    try {
        stream = FileInputStream(path)
        bitmap = BitmapFactory.decodeStream(BufferedInputStream(stream), null, opts)
    } catch (e: Exception) {
        Log.e("BitmapFactory", "Unable to decode stream: $e")
    } finally {
        if (stream != null) {
            try {
                stream.close()
            } catch (e: IOException) {
                // do nothing here
            }

        }
    }
    return bitmap
}

/**
 * 文件是否是图片
 * 注意 获取时需要采用转换过的路径file
 */
fun File.isImage(): Boolean {
    val file = this
    return if (!file.exists()) {
        false
    } else file.path.isImage()
}

/**
 * 文件路径是否是图片
 * 注意 获取时需要采用转换过的路径
 */
fun String.isImage(): Boolean {
    val filePath = this
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    return try {
        BitmapFactory.decodeFile(filePath, options)
        options.outWidth != -1 && options.outHeight != -1
    } catch (e: Exception) {
        false
    }
}

private fun isEmptyBitmap(src: Bitmap?): Boolean {
    return src == null || src.width == 0 || src.height == 0
}

/**
 * 资源转换为bitmap
 */
fun Int.toBitmap(): Bitmap? {
    val resources = app.resources
    val res = this
    return BitmapFactory.decodeResource(resources, this)
}
