package com.atcc.logger

interface LogInterceptor {

    fun intercept(logType: LogType, tag: String, msg: String, tr: Throwable?)
}
