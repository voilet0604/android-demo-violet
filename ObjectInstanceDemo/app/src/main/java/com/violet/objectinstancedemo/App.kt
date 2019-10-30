package com.violet.objectinstancedemo

import android.app.Application
import com.violet.objectinstancedemo.log.CrashReportingTree
import com.zhongyi.meetingcheckin.log.ThreadAwareDebugTree
import timber.log.Timber

/**
 * @Author: shalei
 * @Date: 2019-10-30 10:47
 * @Desc:
 **/
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initLog()
    }

    private fun initLog() {
        Timber.plant(if (BuildConfig.DEBUG) ThreadAwareDebugTree() else CrashReportingTree())
    }
}