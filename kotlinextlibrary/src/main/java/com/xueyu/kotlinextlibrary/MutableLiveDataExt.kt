package com.xueyu.kotlinextlibrary

import androidx.lifecycle.MutableLiveData

/**
 * MutableLiveData扩展函数
 *
 * @author wm
 * @date 19-8-22
 */
fun <T : Any> MutableLiveData<T>.set(value: T?) = postValue(value)

fun <T : Any> MutableLiveData<T>.get() = value

