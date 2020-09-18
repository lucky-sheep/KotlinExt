package com.xueyu.kotlinextlibrary

import android.os.CountDownTimer
import java.util.*

/**
 * Time
 *
 * @author wm
 * @date 20-6-13
 */
fun createCountDown(
    millisInFuture: Long = 60000,
    countDownInterval: Long = 1000,
    onFinish: ((reset: String) -> Unit),
    onTick: ((millisUntilFinished: Long) -> Unit)? = null,
    onHMS: ((hour: String, minute: String, second: String) -> Unit)? = null
): CountDownTimer = object : CountDownTimer(millisInFuture, countDownInterval) {

    override fun onTick(millisUntilFinished: Long) {
        onTick?.invoke(millisUntilFinished)
        onHMS?.let {
            val hour = TimeExt.getPartTimeFromSeconds(millisUntilFinished / 1000, 1)
            val minute = TimeExt.getPartTimeFromSeconds(millisUntilFinished / 1000, 2)
            val second = TimeExt.getPartTimeFromSeconds(millisUntilFinished / 1000, 3)
            val tvHour =
                if (hour < 10) String.format(Locale.getDefault(), "0%d", hour) else hour.toString()
            val tvMinute =
                if (minute < 10) String.format(
                    Locale.getDefault(),
                    "0%d",
                    minute
                ) else minute.toString()
            val tvSecond =
                if (second < 10) String.format(
                    Locale.getDefault(),
                    "0%d",
                    second
                ) else second.toString()
            it.invoke(tvHour, tvMinute, tvSecond)
        }
    }

    override fun onFinish() {
        cancel()
        onFinish.invoke("00")
    }
}

object TimeExt {
    fun getPartTimeFromSeconds(second: Long, type: Int): Long {
        var h: Long = 0
        var d: Long = 0
        var s: Long = 0
        val temp = second % 3600
        if (second > 3600) {
            h = second / 3600
            if (temp != 0L) {
                if (temp > 60) {
                    d = temp / 60
                    if (temp % 60 != 0L) {
                        s = temp % 60
                    }
                } else {
                    s = temp
                }
            }
        } else {
            d = second / 60
            if (second % 60 != 0L) {
                s = second % 60
            }
        }
        return when (type) {
            1 -> h
            2 -> d
            3 -> s
            else -> 0
        }
    }
}



