package com.penumbraos.appprocessmocks

import android.app.ActivityThread
import android.os.Looper

@Suppress("DEPRECATION")
class Common {
    companion object {
        /**
         * Set up the `app_process` environment to mirror that of a native app. You should call `MockContext.createWithAppContext` first
         */
        fun initialize(classLoader: ClassLoader): ActivityThread {
            Looper.prepareMainLooper()

            // The android.jar plugin currently isn't letting us use these classes directly
            val activityThreadClass = classLoader.loadClass("android.app.ActivityThread")
            val activityThreadConstructor = activityThreadClass.getDeclaredConstructor()
            activityThreadConstructor.isAccessible = true
            val mainActivityThread = activityThreadConstructor.newInstance() as ActivityThread

            ActivityThread.initializeMainlineModules()

            return mainActivityThread
        }
    }
}