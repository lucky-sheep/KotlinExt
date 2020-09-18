package com.xueyu.kotlinextlibrary

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.util.TypedValue
import android.view.WindowManager
import com.xueyu.kotlinextlibrary.core.app
import java.security.MessageDigest
import java.util.*
import java.util.UUID.randomUUID

/**
 * 设备相关扩展函数
 *
 * @author wm
 * @date 20-2-13
 */
/**
 * 顶部状态栏高度
 *
 * @return Int
 */
val statusBarHeight: Int
    get() {
        if (HljExtAttrs.isIgnoreStatusBar) {
            return 0
        }
        var result = 0
        val resourceId = app.resources
            .getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = app.resources
                .getDimensionPixelSize(resourceId)
        }
        return result
    }

/**
 * 一般情况下的statusBarHeight+45.dp
 */
val toolbarHeight: Int = statusBarHeight + 45.dp

/**
 * 底部状态栏高度
 *
 * @return Int
 */
val navigationBarHeight: Int
    get() {
        val rid = app.resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return if (rid != 0 && app.resources.getBoolean(rid)) {
            val heightRid = app.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            app.resources.getDimensionPixelSize(heightRid);
        } else
            0
    }



/**
 * 屏幕宽高
 *
 * @return Point
 */
val deviceSize: Point
    get() {
        val deviceSize = Point()
        val wm = app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        display.getSize(deviceSize)
        return deviceSize
    }

/**
 * 屏幕宽
 *
 * @return Int
 */
val deviceWidth: Int
    get() = deviceSize.x

/**
 * 屏幕高
 *
 * @return Int
 */
val deviceHeight: Int
    get() = deviceSize.y

/**
 * 底部是否有状态栏显示(可能因为机型有一定误差，使用时请谨慎)
 *
 * @return Boolean true:显示 false:不显示
 */
val isNavBarOnBottom: Boolean
    get() {
        val res = app.resources
        val cfg = res.configuration
        val dm = res.displayMetrics
        val canMove = dm.widthPixels != dm.heightPixels && cfg.smallestScreenWidthDp < 600
        return !canMove || dm.widthPixels < dm.heightPixels
    }

/**
 * dp转px Int
 */
val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        app.resources.displayMetrics
    ).toInt()

/**
 * dp转px Float
 */
val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        app.resources.displayMetrics
    )

/**
 * 获取当前手机系统版本号
 *
 * @return  系统版本号
 */
val systemVersion: String
    get() = Build.VERSION.RELEASE

/**
 * 获取手机型号
 *
 * @return  手机型号
 */
val systemModel: String
    get() = Build.MODEL

/**
 * 获取手机厂商
 *
 * @return  手机厂商
 */
val deviceBrand: String
    get() = Build.BRAND

/**
 * 获得设备的AndroidId(不推荐)
 *
 * @return 设备的AndroidId
 */
val androidId: String
    @SuppressLint("HardwareIds")
    get() {
        try {
            return Settings.Secure.getString(app.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

/**
 * 获得设备序列号（如：WTK7N16923005607）, 个别设备无法获取
 *
 * @return 设备序列号
 */
val SERIAL: String
    @SuppressLint("HardwareIds")
    get() {
        try {
            return Build.SERIAL
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

/**
 * 获得设备硬件uuid
 * 使用硬件信息，计算出一个随机数
 *
 * @return 设备硬件uuid
 */
val UUID: String
    @SuppressLint("HardwareIds")
    get() {
        try {
            val dev = "3883756" +
                    Build.BOARD.length % 10 +
                    Build.BRAND.length % 10 +
                    Build.DEVICE.length % 10 +
                    Build.HARDWARE.length % 10 +
                    Build.ID.length % 10 +
                    Build.MODEL.length % 10 +
                    Build.PRODUCT.length % 10 +
                    Build.SERIAL.length % 10
            return UUID(
                dev.hashCode().toLong(),
                Build.SERIAL.hashCode().toLong()
            ).toString()
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
    }

/**
 * deviceId
 */
val deviceId: String
    get() {
        val sbDeviceId = StringBuilder()
        val androidId = androidId
        if (androidId.isNotEmpty()) {
            sbDeviceId.append(androidId)
            sbDeviceId.append("|")
        }
        val serial = SERIAL
        if (serial.isNotEmpty()) {
            sbDeviceId.append(serial)
            sbDeviceId.append("|")
        }
        val uuid = UUID
        if (uuid.isNotEmpty()) {
            sbDeviceId.append(uuid)
        }
        if (sbDeviceId.isNotEmpty()) {
            try {
                val hash = getHashByString(sbDeviceId.toString())
                val sha1 = bytesToHexString(hash)
                if (!sha1.isNullOrEmpty()) {
                    return sha1
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return randomUUID().toString().replace("-", "")
    }

private fun getHashByString(data: String): ByteArray {
    return try {
        val messageDigest = MessageDigest.getInstance("SHA1")
        messageDigest.reset()
        messageDigest.update(data.toByteArray())
        messageDigest.digest()
    } catch (e: Exception) {
        "".toByteArray()
    }
}

private fun bytesToHexString(src: ByteArray?): String? {
    val stringBuilder = StringBuilder("")
    if (src == null || src.isEmpty()) {
        return null
    }
    for (element in src) {
        val v = element.toInt() and 0xFF
        val hv = Integer.toHexString(v)
        if (hv.length == 1) {
            stringBuilder.append(0)
        }
        stringBuilder.append(hv)
    }
    return stringBuilder.toString()
}

