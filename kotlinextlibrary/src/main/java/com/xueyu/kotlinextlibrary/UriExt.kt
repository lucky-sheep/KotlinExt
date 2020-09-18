package com.xueyu.kotlinextlibrary

import android.content.ContentResolver
import android.net.Uri
import com.xueyu.kotlinextlibrary.core.app

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun Uri.isExternalStorageDocument(): Boolean {
    return "com.android.externalstorage.documents" == authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun Uri.isDownloadsDocument(): Boolean {
    return "com.android.providers.downloads.documents" == authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
fun Uri.isMediaDocument(): Boolean {
    return "com.android.providers.media.documents" == authority
}

/**
 *  获取raw资源文件的Uri
 */
fun parseRaw(resource: Int): Uri = Uri.parse(
    ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + app.packageName + "/" + resource
)