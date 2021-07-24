package com.minginovich.pomodoro

const val INTERVAL = 50L
const val START_TIME = "00:00:00"
const val INVALID = "INVALID"
const val COMMAND_START = "COMMAND_START"
const val COMMAND_STOP = "COMMAND_STOP"
const val COMMAND_ID = "COMMAND_ID"
const val STARTED_TIMER_TIME_MS = "STARTED_TIMER_TIME"
const val STARTED_STOPWATCH_ID = "STARTED_STOPWATCH_ID"
const val STARTED_STOPWATCH_BALANCE_MS = "STARTED_STOPWATCH_BALANCE_MS"
const val STARTED_STOPWATCH_START_MS = "STARTED_STOPWATCH_START_MS"
const val PERIOD = 1000L * 60L * 60L * 24L // Day
const val TAG = "TAG"

fun Long.displayTime(): String {
    if (this <= 0L) {
        return START_TIME
    }
    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60

    return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
}

private fun displaySlot(count: Long): String {
    return if (count / 10L > 0) {
        "$count"
    } else {
        "0$count"
    }
}