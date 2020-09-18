package com.xueyu.kotlinextlibrary

import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.text.TextUtils
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.xueyu.kotlinextlibrary.core.app

/**
 * 资源扩展函数
 *
 * @author wm
 * @date 19-8-20
 */
fun color(id: Int) = ContextCompat.getColor(app.applicationContext, id)

fun colorStateList(id: Int) = ContextCompat.getColorStateList(app.applicationContext, id)

fun string(id: Int): String = app.resources.getString(id)

fun stringArray(id: Int): Array<String> = app.resources.getStringArray(id)

fun drawable(id: Int) = ContextCompat.getDrawable(app.applicationContext, id)

fun insetDrawable(
    id: Int,
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0
): InsetDrawable {
    return InsetDrawable(drawable(id), left, top, right, bottom)
}

fun insetDrawable(
    id: Int,
    horizontalInset: Int,
    verticalInset: Int
): InsetDrawable {
    return insetDrawable(id, horizontalInset, verticalInset, horizontalInset, verticalInset)
}

fun dimen(id: Int) = app.resources.getDimension(id)

/**
 * theme
 */
fun getAttrBoolean(attr: Int, defValue: Boolean?): Boolean {
    var a: TypedArray? = null
    try {
        val attrs = intArrayOf(attr)
        a = app.theme.obtainStyledAttributes(attrs)
        return a!!.getBoolean(0, defValue!!)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        a?.recycle()
    }
    return defValue!!
}

@ColorInt
fun getAttrColor(attr: Int, @ColorInt defValue: Int): Int {
    var a: TypedArray? = null
    try {
        val attrs = intArrayOf(attr)
        a = app.theme.obtainStyledAttributes(attrs)
        return a!!.getColor(0, defValue)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        a?.recycle()
    }
    return defValue
}

fun getAttrColorList(attr: Int): ColorStateList? {
    var a: TypedArray? = null
    try {
        val attrs = intArrayOf(attr)
        a = app.theme.obtainStyledAttributes(attrs)
        return a!!.getColorStateList(0)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        a?.recycle()
    }
    return null
}


@DrawableRes
fun getAttrResourceId(attr: Int, @DrawableRes defValue: Int): Int {
    var a: TypedArray? = null
    try {
        val attrs = intArrayOf(attr)
        a = app.theme.obtainStyledAttributes(attrs)
        return a!!.getResourceId(0, defValue)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        a?.recycle()
    }
    return defValue
}


@ColorInt
fun parseColor(colorStr: String): Int {
    if (TextUtils.isEmpty(colorStr)) {
        return Color.BLACK
    }
    try {
        if (colorStr.startsWith("#") || colorStr.length == 7) {
            var color = java.lang.Long.parseLong(colorStr.substring(1), 16)
            if (colorStr.length == 7) {
                color = color or -0x1000000
            }
            return color.toInt()
        }
    } catch (ignored: Exception) {

    }
    return Color.BLACK
}

fun Drawable.tint(color: Int): Drawable {
    DrawableCompat.setTint(this, color)
    return this
}

fun Drawable.tintList(colorStateList: ColorStateList): Drawable {
    DrawableCompat.setTintList(this, colorStateList)
    return this
}