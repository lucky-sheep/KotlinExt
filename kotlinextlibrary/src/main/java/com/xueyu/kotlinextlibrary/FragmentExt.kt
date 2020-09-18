package com.xueyu.kotlinextlibrary

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Fragment扩展函数
 *
 * @author wm
 * @date 19-8-20
 */
/**
 * 添加fragment到activity
 */
/**
 * startActivityForResult
 */
inline fun <reified T : Any> Fragment.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(requireContext())
    intent.init()
    startActivityForResult(intent, requestCode, options)
}