package com.xueyu.kotlinextlibrary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * activity扩展函数
 *
 * @author wm
 * @date 20-2-13
 */
/**
 * startActivity
 */
inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
}

/**
 * startActivityForResult
 */
inline fun <reified T : Any> FragmentActivity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, requestCode, options)
}

/**
 * finishActivityWithResult
 */
fun FragmentActivity.finishActivityWithResult(
    extras: Intent.() -> Unit = {}
) {
    val intent = Intent()
    intent.extras()
    setResult(Activity.RESULT_OK, intent)
    finish()
}

/**
 * 创建Intent
 */
inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)





