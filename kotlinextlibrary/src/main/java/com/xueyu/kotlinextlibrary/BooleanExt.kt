package com.xueyu.kotlinextlibrary


/**
 * Boolean扩展函数
 */
fun <T> judge(boolean: Boolean,success: () -> T?, fail: () -> T?): T? =
    if (boolean) success.invoke() else fail.invoke()

fun <T> judgeNotNull(boolean: Boolean,success: () -> T, fail: () -> T): T =
    if (boolean) success.invoke() else fail.invoke()
