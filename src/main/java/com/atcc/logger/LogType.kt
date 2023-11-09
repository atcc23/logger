package com.atcc.logger

sealed class LogType(val type: String) {
    object ERROR : LogType("E")
    object DEBUG : LogType("D")
    object WARNING : LogType("W")
    object INFO : LogType("I")
    object VERBOSE : LogType("V")
}