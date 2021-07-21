package com.minginovich.pomodoro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.minginovich.pomodoro.databinding.ActivityMainBinding
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), StopwatchListener, TimePickerFragment.TimePickerCallback, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    private val stopwatchAdapter = StopwatchAdapter(this)
    private val stopwatches = mutableListOf<Stopwatch>()
    private var startedStopwatch: Int = -1
    private var nextId = 0
    private var startTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.addNewStopwatchButton.setOnClickListener {
            TimePickerFragment().show(supportFragmentManager, "timePicker")
        }


        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        startTime = System.currentTimeMillis()

        lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                binding.timerView.text = (System.currentTimeMillis() - startTime).displayTime()
                delay(INTERVAL)
            }
        }
    }

    //StopwatchListener
    override fun start(id: Int, startMs: Long, balanceMs: Long) {
        changeStopwatch(id, startMs , balanceMs, true)
    }

    //StopwatchListener
    override fun stop(id: Int, startMs: Long, balanceMs: Long) {
        changeStopwatch(id, startMs , balanceMs, false)
    }

    //StopwatchListener
    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    //StopwatchListener
    override fun getStartedStopwatch(): Int {
        return startedStopwatch
    }

    //StopwatchListener
    override fun setStartedStopwatch(stopwatchId: Int) {
        startedStopwatch = stopwatchId
    }

    private fun changeStopwatch(id: Int, startMs: Long, balanceMs: Long, isStarted: Boolean) {
        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if (it.id == id) {
                newTimers.add(Stopwatch(it.id, startMs, balanceMs, isStarted))
            } else {
                newTimers.add(it)
            }
        }
        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, startTime)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }


    /////TimePickerFragment.TimePickerCallback
    override fun userChoice(timeMs: Long) {
        Log.d("classTimePickerFragment", "userChoice: time=$timeMs")
        if (timeMs > 0) {
            Log.d("classTimePickerFragment", "user: time=$timeMs")
            stopwatches.add(Stopwatch(nextId++, timeMs, timeMs, false))
            stopwatchAdapter.submitList(stopwatches.toList())
        }
    }

    private companion object {
        private const val INTERVAL = 10L
    }

}