package com.minginovich.pomodoro

interface StopwatchListener {
    fun start(id: Int)
    fun stop(id: Int, currentMs: Long)
    fun reset(id: Int)
    fun delete(id: Int)
}