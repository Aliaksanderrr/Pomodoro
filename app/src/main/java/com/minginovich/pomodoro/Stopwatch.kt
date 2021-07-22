package com.minginovich.pomodoro

data class Stopwatch(
    val id: Int,
    val startMs: Long,
    var balanceMs: Long,
    var isStarted: Boolean
)
