package com.xueyu.kotlinextlibrary

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.text.TextUtils
import com.xueyu.kotlinextlibrary.core.app
import java.io.File

/**
 * media相关扩展函数
 *
 * @author wm
 * @date 20-2-24
 */
/**
 * 多媒体文件夹中的文件展示在相册中
 */
fun String?.publish() {
    val path = this
    if (!TextUtils.isEmpty(path)) {
        val mMediaScanner = MediaScannerConnection(
            app, null
        )
        mMediaScanner.connect()
        if (mMediaScanner.isConnected) {
            mMediaScanner.scanFile(path, null)
        }
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val uri = Uri.fromFile(File(path))
        intent.data = uri
        app.sendBroadcast(intent)
    }
}

/**
 * 获取视频某一帧
 */
fun String.getFrameAt(timeMs: Long): Bitmap? {
    val path = this
    return MediaMetadataRetriever().apply {
        try {
            setDataSource(app, Uri.parse(path))
            extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        } catch (e: Exception) {
        }
    }.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST)
}