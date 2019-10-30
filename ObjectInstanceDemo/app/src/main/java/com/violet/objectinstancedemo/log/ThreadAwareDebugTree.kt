package com.zhongyi.meetingcheckin.log

import timber.log.Timber

/**
 * 打印线程名称和行号
 */
class ThreadAwareDebugTree : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        var nTag = tag
        if (null != tag) {
            val threadName = Thread.currentThread().name
            nTag = "<$threadName> $tag"
        }
        super.log(priority, nTag, message, t)
    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        return "${super.createStackElementTag(element)} (Line: ${element.lineNumber})"
    }
}