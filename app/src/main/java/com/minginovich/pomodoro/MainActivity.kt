package com.minginovich.pomodoro

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.minginovich.pomodoro.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), StopwatchListener, TimePickerFragment.TimePickerCallback {

    private lateinit var binding: ActivityMainBinding

    private val stopwatchAdapter = StopwatchAdapter(this)
    private val stopwatches = mutableListOf<Stopwatch>()
    private var nextId = 0

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


    /////TimePickerFragment.TimePickerCallback
    override fun userChoice(timeMs: Long) {
        Log.d("classTimePickerFragment", "userChoice: time=$timeMs")
        if (timeMs > 0) {
            Log.d("classTimePickerFragment", "user: time=$timeMs")
            stopwatches.add(Stopwatch(nextId++, timeMs, timeMs, false))
            stopwatchAdapter.submitList(stopwatches.toList())
        }
    }

}