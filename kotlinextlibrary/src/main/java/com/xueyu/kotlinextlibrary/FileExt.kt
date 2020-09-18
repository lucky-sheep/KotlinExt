package com.xueyu.kotlinextlibrary

import android.graphics.Bitmap
import android.os.Environment
import android.text.TextUtils
import com.xueyu.kotlinextlibrary.core.app
import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * file相关扩展函数
 *
 * @author wm
 * @date 20-2-19
 */
/**
 * sdCard路径
 */
val sdCardPath: String
    get() = Environment.getExternalStorageDirectory().absolutePath

/**
 * 创建sdCard路径的filePath
 */
fun String.toSdCardFilePath(): String {
    return "${sdCardPath}/$this"
}

/**
 * 多媒体路径
 */
val DICMPath: String
    get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        .absolutePath

/**
 * 多媒体路径filePath
 */
fun String.toDICMFilePath(): String {
    return "${DICMPath}/$this"
}

/**
 * 缓存路径
 */
val cachePath: String
    get() = app.cacheDir.absolutePath

/**
 * 缓存路径filePath
 */
fun String.toCacheFilePath(): String {
    return "${cachePath}/$this"
}

/**
 * 在sd卡中创建文件
 */
fun String?.createFileInSdCardDir(
    dir: String = "${app.packageName}sd"
): String? {
    val dirPath = dir.toSdCardFilePath()
    return if (dirPath.createDir()) {
        if ("$dirPath/$this".createFile()) {
            "$dirPath/$this"
        } else {
            null
        }
    } else {
        null
    }
}

/**
 * 在缓存文件夹中创建文件
 */
fun String?.createFileInCacheDir(
    dir: String = "${app.packageName}cache"
): String? {
    val dirPath = dir.toCacheFilePath()
    return if (dirPath.createDir()) {
        if ("$dirPath/$this".createFile()) {
            "$dirPath/$this"
        } else {
            null
        }
    } else {
        null
    }
}

/**
 * 在多媒体文件夹中创建文件
 */
fun String.createFileInDICMDir(
    dir: String = "Camera"
): String? {
    val dirPath = dir.toDICMFilePath()
    return if (dirPath.createDir()) {
        if ("$dirPath/$this".createFile()) {
            "$dirPath/$this"
        } else {
            null
        }
    } else {
        null
    }
}

/**
 * 获取文件类型
 */
val String?.typeName: String
    get() {
        if (TextUtils.isEmpty(this)) {
            return ""
        }
        this!!.trim { it <= ' ' }
        val s = split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (s.size >= 2) {
            s[s.size - 1]
        } else ""
    }

/**
 * 通过路径获取file
 */
fun String?.toFile(): File? {
    return if (TextUtils.isEmpty(this)) null else File(this)
}

/**
 * 根据路径判断file是否存在
 */
val String?.isFileExists: Boolean
    get() = !TextUtils.isEmpty(this) && toFile()!!.exists()

/**
 * 判断是否是文件夹
 */
val String.isDir: Boolean
    get() = toFile().isDir

/**
 * 判断是否是文件夹
 */
val File?.isDir: Boolean
    get() = this != null && this.exists() && this.isDirectory

/**
 * 创建文件夹，如果存在则不操作
 */
fun String.createDir(): Boolean {
    return toFile().createDir()
}

/**
 * 创建文件夹，如果存在则不操作
 */
fun File?.createDir(): Boolean {
    return this != null && if (this.exists()) this.isDirectory else this.mkdirs()
}

/**
 * 创建文件如果有旧的文件则删除旧的替换为新的
 */
fun String?.createFile(): Boolean {
    if (TextUtils.isEmpty(this)) return false
    return toFile().createFile()
}

/**
 * 创建文件如果有旧的文件则删除旧的替换为新的
 */
fun File?.createFile(): Boolean {
    if (this == null) return false
    if (exists()) {
        delete()
    }
    return try {
        createNewFile()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 删除文件
 */
fun String?.deleteFile(): Boolean {
    return toFile().deleteFile()
}

/**
 * 删除文件
 */
fun File?.deleteFile(): Boolean {
    return this != null && (!this.exists() || this.isFile && this.delete())
}

/**
 * 删除文件夹
 */
fun String?.deleteDir(): Boolean {
    return toFile().deleteDir()
}

/**
 * 删除文件夹
 */
fun File?.deleteDir(): Boolean {
    if (this == null) return false
    if (!exists()) return true
    if (!isDir) return false
    val files = listFiles()
    if (!files.isNullOrEmpty()) {
        for (file in files) {
            if (file.isFile) {
                if (!file.delete()) return false
            } else if (file.isDir) {
                if (!file.deleteDir()) return false
            }
        }
    }
    return delete()
}

/**
 * 获取文件名称
 */
val File?.fileName: String
    get() = this?.absolutePath?.fileName ?: ""

/**
 * 获取文件名称
 */
val String?.fileName: String
    get() {
        val filePath = this
        if (TextUtils.isEmpty(filePath)) return ""
        val lastSep = filePath!!.lastIndexOf(File.separator)
        val typeName = filePath.typeName
        return if (lastSep == -1 && lastSep + 1 >= filePath.length) "" else filePath.substring(
            lastSep + 1,
            filePath.length
        ).replace(".${typeName}", "")
    }

/**
 * 保存bitmap到file
 *
 * @return true:保存成功 false:保存失败
 */
fun Bitmap.saveToFile(
    filePath: String?,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
): String? {
    return this.saveToFile(filePath.toFile(), format)
}

/**
 * 保存bitmap到file
 *
 * @return true:保存成功 false:保存失败
 */
fun Bitmap.saveToFile(
    file: File?,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
): String? {
    val src = this
    var path: String? = null
    if (isEmptyBitmap(src) || !createOrExistsFile(file)) {
        return path
    }
    var os: OutputStream? = null
    try {
        os = BufferedOutputStream(FileOutputStream(file))
        val compress = src.compress(format, 100, os)
        if (compress) {
            path = file!!.absolutePath
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            os?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return path
}

fun String?.readFile2Bytes(): ByteArray? {
    return this.toFile().readFile2Bytes()
}

/**
 * 从文件中获取bytes
 */
fun File?.readFile2Bytes(): ByteArray? {
    var fc: FileChannel? = null
    try {
        fc = RandomAccessFile(this, "r").channel
        val size = fc.size()
        val mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size).load()
        val data = ByteArray(size.toInt())
        mbb.get(data, 0, size.toInt())
        return data
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    } finally {
        try {
            fc?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

/**
 * 将bytes写入文件
 */
fun String?.writeFileFromBytes(
    bytes: ByteArray
): Boolean {
    val filePath = this
    return filePath.toFile().writeFileFromBytes(bytes)
}

/**
 * 将bytes写入文件
 */
fun File?.writeFileFromBytes(bytes: ByteArray): Boolean {
    var fc: FileChannel? = null
    val file = this
    if (!createOrExistsFile(file)) return false
    return try {
        fc = FileOutputStream(this, false).channel
        if (fc == null) {
            false
        } else {
            fc.write(ByteBuffer.wrap(bytes))
            fc.force(true)
            true
        }
    } catch (e: IOException) {
        e.printStackTrace()
        false
    } finally {
        try {
            fc?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

/**
 * 将inputStream写入文件
 */
fun String?.writeFileFromIS(
    inputStream: InputStream,
    append: Boolean
): Boolean {
    val filePath = this
    return filePath.toFile().writeFileFromIS(inputStream, append)
}

/**
 * 将inputStream写入文件
 */
fun File?.writeFileFromIS(
    inputStream: InputStream,
    append: Boolean
): Boolean {
    val file = this
    if (!createOrExistsFile(file)) return false
    var os: OutputStream? = null
    try {
        os = BufferedOutputStream(FileOutputStream(file, append))
        val data = ByteArray(8192)
        var len = 0
        while (len != -1) {
            len = inputStream.read(data, 0, 8192)
            if (len != -1) {
                os.write(data, 0, len)
            }
        }
        return true
    } catch (e: Exception) {
        return false
    } finally {
        try {
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            os?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

/**
 * 将inputStream转化为bytes
 */
fun InputStream?.toBytes(): ByteArray? {
    val inputStream = this ?: return null
    var os: ByteArrayOutputStream? = null
    try {
        os = ByteArrayOutputStream()
        val data = ByteArray(8192)
        var len = 0
        while (len != -1) {
            len = inputStream.read(data, 0, 8192)
            if (len != -1) {
                os.write(data, 0, len)
            }
        }
        return os.toByteArray()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        try {
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            os?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}

private fun createOrExistsFile(file: File?): Boolean {
    if (file == null) return false
    if (file.exists()) return file.isFile
    if (!createOrExistsDir(file.parentFile)) return false
    return try {
        file.createNewFile()
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

private fun createOrExistsDir(file: File?): Boolean {
    return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
}

/**
 * 从file中获取bitmap
 */
fun String.getBitmap(): Bitmap? {
    return this.toFile().getBitmap()
}

/**
 * 从file中获取bitmap
 */
fun File?.getBitmap(): Bitmap? {
    val file = this
    var bitmap: Bitmap? = null
    file?.readFile2Bytes()?.let {
        bitmap = it.toBitmap()
    }
    return bitmap
}

private fun isEmptyBitmap(src: Bitmap?): Boolean {
    return src == null || src.width == 0 || src.height == 0
}




















