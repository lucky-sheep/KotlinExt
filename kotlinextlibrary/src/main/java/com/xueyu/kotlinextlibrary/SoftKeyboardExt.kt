package com.xueyu.kotlinextlibrary

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.xueyu.kotlinextlibrary.findActivity

/**
 * activity或者fragment显示软键盘，不自动顶上
 */
internal fun Activity.setSoftActivityPan() {
    window.setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
    )
}

/**
 * activity或者fragment显示软键盘，自动顶上
 */
internal fun Activity.setSoftActivityResize() {
    window.setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
    )
}

/**
 * activity或者fragment显示软键盘，不自动顶上
 */
fun EditText.showSoftActivityPan() {
    val edit = this
    edit.context.findActivity()?.setSoftActivityPan()
    edit.requestFocus()
}

/**
 * activity或者fragment显示软键盘，自动顶上
 */
fun EditText.showSoftActivityResize() {
    val edit = this
    edit.context.findActivity()?.setSoftActivityResize()
    edit.requestFocus()
}

fun Activity.setSoftJustPanAndAlwaysHidden() {
    window.setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
    )
}

fun Activity.setSoftJustResizeAndAlwaysHidden() {
    window.setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
    )
}

internal fun Context.showSoftToggle(edit: EditText, delay: Long = 10L) {
    edit.requestFocus()
    edit.isFocusable = true
    edit.isFocusableInTouchMode = true
    edit.postDelayed({
        edit.requestLayout()
        val systemService = applicationContext
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        systemService.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }, delay)
}

/**
 * dialog 显示软键盘，不自动顶上
 */
fun EditText.showSoftDialogPan() {
    val edit = this
    edit.context.findActivity()?.let {
        it.setSoftJustPanAndAlwaysHidden()
        it.showSoftToggle(edit, 10L)
    }
}

/**
 * dialog 显示软键盘，自动顶上
 */
fun EditText.showSoftDialogResize() {
    val edit = this
    edit.context.findActivity()?.let {
        it.setSoftJustResizeAndAlwaysHidden()
        it.showSoftToggle(edit, 10L)
    }
}

/**
 * 隐藏软键盘
 */
fun Activity.forceHideSoftInput() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0)
}
