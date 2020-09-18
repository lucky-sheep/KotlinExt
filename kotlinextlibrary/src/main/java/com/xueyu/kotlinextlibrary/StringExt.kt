@file:JvmName("StringKit")

package com.xueyu.kotlinextlibrary

import android.text.TextUtils
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * String扩展函数
 *
 * @author wm
 * @date 19-8-20
 */

/**
 * 是否是中文字符
 */
fun String.isChinese() =
    if (TextUtils.isEmpty(this)) false else "^[\u4E00-\u9FA5]+$".toRegex().matches(this)

/**
 * 是否是httpUrl
 */
fun String?.isHttpUrl(): Boolean {
    return !TextUtils.isEmpty(this) && (this?.startsWith("http://") ?: false || (this?.startsWith("https://")
        ?: false))
}

/**
 * 转化为md5
 */
fun String.toMd5(): String {
    try {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        val digest: ByteArray = instance.digest(toByteArray())
        val sb = StringBuffer()
        for (b in digest) {
            val i: Int = b.toInt() and 0xff
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                hexString = "0$hexString"
            }
            sb.append(hexString)
        }
        return sb.toString()

    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}

fun String?.firstElement() = if (this.isNullOrEmpty()) "" else this.subSequence(0, 1)

