package com.atcc.logger

import android.util.Log

private const val MAX_TAG_LENGTH = 23
private const val CLASS_STACK_ITEM = 4
private const val PREFIX = "ATCC"

object Logger {

    var isLoggable: Boolean = false

    private val tag: String
        get() {
            val caller = Thread.currentThread().stackTrace[CLASS_STACK_ITEM]
            var tag = caller.className
            val lastDot = tag.lastIndexOf('.')
            if (lastDot > 0) {
                tag = tag.substring(lastDot + 1)
            }
            if (tag.length > MAX_TAG_LENGTH) {
                tag = tag.substring(0, MAX_TAG_LENGTH)
            }
            return "$PREFIX:$tag"
        }

    private val interceptors = mutableListOf<LogInterceptor>()

    fun v(msg: String) {
        if (isLoggable) Log.v(tag, msg)

        onDump(LogType.VERBOSE, tag, msg)
    }

    fun v(msg: String?, tr: Throwable?) {
        if (isLoggable) Log.v(tag, msg, tr)

        onDump(LogType.VERBOSE, tag, getStackTraceString(msg, tr), tr)
    }

    fun d(msg: String) {
        if (isLoggable) Log.d(tag, msg)

        onDump(LogType.DEBUG, tag, msg)
    }

    fun d(msg: String?, tr: Throwable?) {
        if (isLoggable) Log.d(tag, msg, tr)

        onDump(LogType.DEBUG, tag, getStackTraceString(msg, tr), tr)
    }

    fun i(msg: String) {
        if (isLoggable) Log.i(tag, msg)

        onDump(LogType.INFO, tag, msg)
    }

    fun i(msg: String?, tr: Throwable?) {
        if (isLoggable) Log.i(tag, msg, tr)

        onDump(LogType.INFO, tag, getStackTraceString(msg, tr), tr)
    }

    fun w(msg: String) {
        if (isLoggable) Log.w(tag, msg)

        onDump(LogType.WARNING, tag, msg)
    }

    fun w(msg: String?, tr: Throwable?) {
        if (isLoggable) Log.w(tag, msg, tr)

        onDump(LogType.WARNING, tag, getStackTraceString(msg, tr), tr)
    }

    fun e(msg: String) {
        if (isLoggable) Log.e(tag, msg)

        onDump(LogType.ERROR, tag, msg)
    }

    fun e(msg: String?, tr: Throwable?) {
        if (isLoggable) Log.e(tag, msg, tr)

        onDump(LogType.ERROR, tag, getStackTraceString(msg, tr), tr)
    }

    private fun getStackTraceString(msg: String?, tr: Throwable?): String {
        val message = msg?.run { "$this \n" } ?: ""
        val stackTrace = Log.getStackTraceString(tr)
        return message + stackTrace
    }

    private fun onDump(logType: LogType, tag: String, msg: String, tr: Throwable? = null) {
        interceptors.forEach {
            it.intercept(logType, tag, msg, tr)
        }
    }

    fun addLogInterceptor(interceptor: LogInterceptor) {
        interceptors.add(interceptor)
    }

}
