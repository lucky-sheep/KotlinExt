package com.xueyu.kotlinextlibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.xueyu.kotlinextlibrary.core.app
import java.net.URISyntaxException

/**
 * Context扩展函数
 *
 * @author wm
 * @date 19-8-14
 */
/**
 * 进程是否运行
 */
fun Context.appIsRunning(): Boolean {
    try {
        val pid = android.os.Process.myPid()
        val packageName = applicationContext.packageName
        val activityManager = applicationContext
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager
            .runningAppProcesses ?: return false
        for (info in appProcesses) {
            if (info.pid != pid) {
                continue
            }
            return info.processName == packageName
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

/**
 * 包名
 */
val packageName: String
    get() = app.packageName

/**
 * 版本名称
 *
 * @return String
 */
val appVersion: String
    get() {
        var appVersion = ""
        try {
            appVersion = app.applicationContext.packageManager
                .getPackageInfo(app.applicationContext.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appVersion
    }

/**
 * 检测网络状态
 * uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"
 *
 * @return true: 网络可用; false: 网络不可用
 */
val isNetworkAvailable: Boolean
    get() {
        val connMgr =
            app.applicationContext.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netWorkInfo = connMgr.activeNetworkInfo ?: return false
        return netWorkInfo.isAvailable
    }

/**
 * 将manifest中的key转化为value
 */
fun manifest(key: String): String? {
    var value: String? = ""
    try {
        val appInfo = app.applicationContext
            .packageManager
            .getApplicationInfo(app.applicationContext.packageName, PackageManager.GET_META_DATA)
        value = appInfo.metaData.getString(key)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return value
}

/**
 * 通过相册获取uri转化为实际路径
 */
@SuppressLint("NewApi")
@Throws(URISyntaxException::class)
fun getPathByUri(path: Uri): String? {
    var uri = path
    val needToCheckUri = Build.VERSION.SDK_INT >= 19
    var selection: String? = null
    var selectionArgs: Array<String>? = null
    if (needToCheckUri && DocumentsContract.isDocumentUri(app.applicationContext, uri)) {
        when {
            uri.isExternalStorageDocument() -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
            uri.isDownloadsDocument() -> {
                val id = DocumentsContract.getDocumentId(uri)
                uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
            }
            uri.isMediaDocument() -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                when (type) {
                    "image" -> uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
            }
        }
    }
    if ("content".equals(uri.scheme!!, ignoreCase = true)) {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor = app.contentResolver
                .query(uri, projection, selection, selectionArgs, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) {
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
    } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
        return uri.path
    }
    return null
}

/**
 * 通过Context获取activity
 */
fun Context.findActivity(): Activity? {
    val context = this
    if (context is Activity) {
        return context
    }
    return if (context is ContextWrapper) {
        context.baseContext.findActivity()
    } else {
        null
    }
}



