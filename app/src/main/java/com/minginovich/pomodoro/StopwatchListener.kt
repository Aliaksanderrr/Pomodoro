package com.minginovich.pomodoro

interface StopwatchListener {
    fun start(id: Int, startMs: Long, balanceMs: Long)
    fun stop(id: Int, startMs: Long, balanceMs: Long)
    fun delete(id: Int)
    fun getStartedStopwatch(): Int
    fun setStartedStopwatch(id: Int)
}