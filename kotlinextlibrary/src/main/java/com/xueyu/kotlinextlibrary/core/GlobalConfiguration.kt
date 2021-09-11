package com.xueyu.kotlinextlibrary.core

import android.app.Application
import com.xueyu.applicationproxy.ConfigModule
import kotlin.properties.Delegates

/**
 * GlobalConfiguration
 *
 * @author wm
 * @date 19-8-19
 */
internal var app: Application by Delegates.notNull()

class GlobalConfiguration : ConfigModule {
    override fun getPriority() = 5

    override fun injectApp(application: Application) {
        app = application
    }

    override fun terminateApp(application: Application) {

    }
}
