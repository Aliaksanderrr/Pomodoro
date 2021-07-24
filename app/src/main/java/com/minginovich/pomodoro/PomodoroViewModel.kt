package com.minginovich.pomodoro

import android.util.Log
import androidx.lifecycle.ViewModel

class PomodoroViewModel: ViewModel() {
    val stopwatches = mutableListOf<Stopwatch>()
    var startedStopwatch: Stopwatch? = null

    var nextId = 0
    var startTime = 0L
}