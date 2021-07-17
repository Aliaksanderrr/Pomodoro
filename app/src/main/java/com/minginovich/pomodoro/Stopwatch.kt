package com.minginovich.pomodoro

data class Stopwatch(
    val id: Int,
    var currentMs: Long,
    val isStarted: Boolean
)
