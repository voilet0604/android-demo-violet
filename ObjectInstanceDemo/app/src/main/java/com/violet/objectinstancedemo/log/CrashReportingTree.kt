package com.violet.objectinstancedemo.log

import timber.log.Timber

/**
 * @Author: shalei
 * @Date: 2019-10-30 10:46
 * @Desc:
 **/
class CrashReportingTree: Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    }
}