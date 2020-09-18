@file:JvmName("TextKit")

package com.xueyu.kotlinextlibrary

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.*
import android.text.style.*
import android.view.View
import androidx.annotation.ColorInt
import com.xueyu.kotlinextlibrary.core.app
import com.xueyu.kotlinextlibrary.drawable
import java.lang.ref.WeakReference

inline fun CharSequence.spannable(block: Spannable.() -> Unit = {}): Spannable {
    return if (this is Spannable) {
        this.apply(block)
    } else {
        SpannableString(this).apply(block)
    }
}

fun CharSequence.spannableBuilder(
    start: Int = 0,
    end: Int = this.length,
    flag: Int = SpannedString.SPAN_INCLUSIVE_EXCLUSIVE
): SpannableStringBuilder {
    return if (this is SpannableStringBuilder) {
        this
    } else {
        SpannableStringBuilder(this, start, end)
    }
}

/**
 * 文字大小
 */
fun Spannable.setSize(
    textSize: Int,
    start: Int = 0,
    end: Int = this.length,
    flag: Int = SpannedString.SPAN_INCLUSIVE_EXCLUSIVE
): Spannable {
    setSpan(AbsoluteSizeSpan(textSize, true), start, end, flag)
    return this
}

/**
 * 文字样式
 * @param style [StyleSpan] 的 style
 */
fun Spannable.setTextStyle(
    style: Int,
    start: Int = 0,
    end: Int = this.length,
    flag: Int = SpannedString.SPAN_INCLUSIVE_EXCLUSIVE
): Spannable {
    setSpan(StyleSpan(style), start, end, flag)
    return this
}

/**
 * 文字颜色
 */
fun Spannable.setForeground(
    @ColorInt color: Int,
    start: Int = 0,
    end: Int = this.length,
    flag: Int = SpannedString.SPAN_INCLUSIVE_EXCLUSIVE
): Spannable {
    setSpan(ForegroundColorSpan(color), start, end, flag)
    return this
}

/**
 * 文字中横线(删除线)
 */
fun Spannable.setStrikethrough(
    start: Int = 0,
    end: Int = this.length,
    flag: Int = SpannedString.SPAN_INCLUSIVE_EXCLUSIVE
): Spannable {
    setSpan(StrikethroughSpan(), start, end, flag)
    return this
}

/**
 * 文字下划线
 */
fun Spannable.setUnderline(
    start: Int = 0,
    end: Int = this.length,
    flag: Int = SpannedString.SPAN_INCLUSIVE_EXCLUSIVE
): Spannable {
    setSpan(UnderlineSpan(), start, end, flag)
    return this
}

/**
 * 文字设置上标
 */
fun Spannable.setSuperscript(
    start: Int = 0,
    end: Int = this.length,
    flag: Int = SpannedString.SPAN_INCLUSIVE_EXCLUSIVE
): Spannable {
    setSpan(SuperscriptSpan(), start, end, flag)
    return this
}

/**
 * 文字设置下标
 */
fun Spannable.setSubscript(
    start: Int = 0,
    end: Int = this.length,
    flag: Int = SpannedString.SPAN_INCLUSIVE_EXCLUSIVE
): Spannable {
    setSpan(SubscriptSpan(), start, end, flag)
    return this
}

/**
 * 文字替换为图片
 */
fun Spannable.setImage(
    drawable: Drawable,
    verticalAlignment: Int = DynamicDrawableSpan.ALIGN_BOTTOM,
    start: Int = 0,
    end: Int = this.length,
    flag: Int = SpannedString.SPAN_INCLUSIVE_EXCLUSIVE
): Spannable {
    setSpan(ImageSpan(drawable, verticalAlignment), start, end, flag)
    return this
}

/**
 * 文字替换为图片
 */
fun Spannable.setImage(
    bitmap: Bitmap,
    verticalAlignment: Int = DynamicDrawableSpan.ALIGN_BOTTOM,
    start: Int = 0,
    end: Int = this.length,
    flag: Int = SpannedString.SPAN_INCLUSIVE_EXCLUSIVE
): Spannable {
    setSpan(ImageSpan(app, bitmap, verticalAlignment), start, end, flag)
    return this
}

private fun Int.startInclusive(): Boolean {
    return this == SpannedString.SPAN_INCLUSIVE_INCLUSIVE
            || this == SpannedString.SPAN_INCLUSIVE_EXCLUSIVE
}

private fun Int.endInclusive(): Boolean {
    return this == SpannedString.SPAN_INCLUSIVE_INCLUSIVE
            || this == SpannedString.SPAN_EXCLUSIVE_INCLUSIVE
}

/**
 * 文字点击
 */
inline fun Spannable.setClickable(
    @ColorInt textColor: Int,
    start: Int = 0,
    end: Int = this.length,
    drawUnderline: Boolean = false,
    flag: Int = SpannedString.SPAN_INCLUSIVE_EXCLUSIVE,
    crossinline clickable: (View) -> Unit
): Spannable {
    setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            clickable(widget)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = textColor
            ds.isUnderlineText = drawUnderline
        }
    }, start, end, flag)
    return this
}

/**
 * 重写 Spannable 的加法运算符
 */
operator fun Spannable.plus(another: CharSequence): SpannableStringBuilder {
    return when {
        this is SpannableStringBuilder -> {
            this.append(another)
            this
        }
        another is SpannableStringBuilder -> {
            another.append(this, 0, this.length)
            another
        }
        else -> {
            SpannableStringBuilder(this) + another
        }
    }
}

fun getPhoneFormatter() = PhoneNumberFormattingTextWatcher()

fun String?.notEmptyString(): String? {
    if (this.isNullOrEmpty()) {
        return null
    }
    return this
}

fun insertDrawableLeft(
    drawable: Drawable,
    string: String,
    bounds: (drawable: Drawable) -> Unit
): SpannableStringBuilder {
    bounds.invoke(drawable)
    val builder = SpannableStringBuilder(" ")
    builder.append(string)
    return builder.also {
        it.setSpan(HljImageSpan(drawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

fun insertDrawableLeft(
    drawableRes: Int,
    string: String,
    bounds: (drawable: Drawable) -> Unit
): SpannableStringBuilder {
    val drawable = drawable(drawableRes)
    val builder = SpannableStringBuilder()
    if (drawable != null) {
        builder.append(" ")
    }
    builder.append(string)
    return if (drawable == null) {
        builder
    } else {
        bounds.invoke(drawable)
        builder.also {
            it.setSpan(HljImageSpan(drawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}

fun insertDrawableLeft(
    drawable: Drawable,
    builder: SpannableStringBuilder,
    bounds: (drawable: Drawable) -> Unit
): SpannableStringBuilder {
    bounds.invoke(drawable)
    return builder.also {
        it.setSpan(HljImageSpan(drawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

fun insertDrawableLeft(
    drawableRes: Int,
    builder: SpannableStringBuilder,
    bounds: (drawable: Drawable) -> Unit
): SpannableStringBuilder {
    val drawable = drawable(drawableRes)
    return if (drawable == null) {
        builder
    } else {
        bounds.invoke(drawable)
        builder.also {
            it.setSpan(HljImageSpan(drawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}

fun insertDrawableRight(
    drawable: Drawable,
    string: String,
    bounds: (drawable: Drawable) -> Unit
): SpannableStringBuilder {
    bounds.invoke(drawable)
    val builder = SpannableStringBuilder(string)
    builder.append(" ")
    return builder.also {
        it.setSpan(
            HljImageSpan(drawable),
            builder.length - 1,
            builder.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

fun insertDrawableRight(
    drawable: Drawable,
    builder: SpannableStringBuilder,
    bounds: (drawable: Drawable) -> Unit
): SpannableStringBuilder {
    bounds.invoke(drawable)
    return builder.also {
        it.setSpan(
            HljImageSpan(drawable),
            builder.length - 1,
            builder.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

fun insertDrawableRight(
    drawableRes: Int,
    string: String,
    bounds: (drawable: Drawable) -> Unit
): SpannableStringBuilder {
    val drawable = drawable(drawableRes)
    val builder = SpannableStringBuilder(string)
    if (drawable != null) {
        builder.append(" ")
    }
    return if (drawable == null) {
        builder
    } else {
        bounds.invoke(drawable)
        builder.also {
            it.setSpan(
                HljImageSpan(drawable),
                builder.length - 1,
                builder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}

fun insertDrawableRight(
    drawableRes: Int,
    builder: SpannableStringBuilder,
    bounds: (drawable: Drawable) -> Unit
): SpannableStringBuilder {
    val drawable = drawable(drawableRes)
    return if (drawable == null) {
        builder
    } else {
        bounds.invoke(drawable)
        builder.also {
            it.setSpan(
                HljImageSpan(drawable),
                builder.length - 1,
                builder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}

internal class HljImageSpan @JvmOverloads constructor(
    private val mDrawable: Drawable,
    verticalAlignment: Int = ALIGN_CENTER
) : DynamicDrawableSpan(verticalAlignment) {

    private val cachedDrawable: Drawable?
        get() {
            val wr = mDrawableRef
            var d: Drawable? = null
            if (wr != null)
                d = wr.get()
            if (d == null) {
                d = drawable
                mDrawableRef = WeakReference(d)
            }
            return d
        }

    private var mDrawableRef: WeakReference<Drawable>? = null

    override fun getDrawable(): Drawable {
        return mDrawable
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val b = cachedDrawable ?: return
        canvas.save()
        val fm = paint.fontMetricsInt
        var transY = y
        when (verticalAlignment) {
            ALIGN_BASELINE -> {
                transY -= paint.fontMetricsInt.descent
                transY += fm.bottom - b.bounds.bottom
            }
            ALIGN_BOTTOM -> transY += fm.bottom - b.bounds.bottom
            else -> {
                val textBottom = y + fm.descent
                val textTop = y + fm.ascent
                transY = textTop + (textBottom - textTop - b.bounds.height()) / 2
                if (transY + b.bounds.bottom > bottom) {
                    transY = bottom - b.bounds.bottom
                }
            }
        }
        canvas.translate(x, transY.toFloat())
        b.draw(canvas)
        canvas.restore()
    }

    companion object {
        const val ALIGN_CENTER = -1
    }
}


