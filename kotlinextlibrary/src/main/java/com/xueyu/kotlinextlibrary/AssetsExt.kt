package com.xueyu.kotlinextlibrary

import android.text.TextUtils
import com.xueyu.kotlinextlibrary.core.app
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Assets扩展函数
 *
 * @author wm
 * @date 20-2-28
 */
/**
 * 从assets中copy到目标文件夹
 */
fun String.copyFileFromAssets(destFilePath: String?): String? {
    val assetsFilePath = this
    if (TextUtils.isEmpty(assetsFilePath)) {
        return null
    }
    return try {
        destFilePath.writeFileFromIS(
            app.assets.open(assetsFilePath),
            false
        )
        destFilePath
    } catch (e: IOException) {
        null
    }
}

/**
 * 从assets中读出string
 */
fun String.readAssets2String(
    charsetName: Charset = Charset.defaultCharset()
): String? {
    val assetsFilePath = this
    val inputStream: InputStream
    try {
        inputStream = app.assets.open(assetsFilePath)
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }

    val bytes = inputStream.toBytes() ?: return null
    return try {
        String(bytes, charsetName)
    } catch (e: Exception) {
        null
    }
}

/**
 * 从file中读出string
 */
fun String.readFile2String(charsetName: Charset = Charset.defaultCharset()): String? {
    val filePath = this
    val bytes = filePath.toFile()?.readFile2Bytes() ?: return null
    return try {
        String(bytes, charsetName)
    } catch (e: Exception) {
        null
    }
}

/**
 * 从Raw中copy到目标文件夹
 */
fun Int.copyFileFromRaw(destFilePath: String?): String? {
    val resId = this
    return try {
        destFilePath.writeFileFromIS(
            app.resources.openRawResource(resId),
            false
        )
        destFilePath
    }catch (e:Exception){
        null
    }
}

/**
 * 从Raw中读出string
 */
fun Int.readRaw2String(charsetName: Charset = Charset.defaultCharset()): String? {
    val resId = this
    val bytes = app.resources.openRawResource(resId).toBytes()?:return null
    return try {
        String(bytes, charsetName)
    } catch (e: Exception) {
        null
    }
}

