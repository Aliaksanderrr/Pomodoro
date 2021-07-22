package com.minginovich.pomodoro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.minginovich.pomodoro.databinding.ActivityMainBinding
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), StopwatchListener, TimePickerFragment.TimePickerCallback, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding

//    private val stopwatches = mutableListOf<Stopwatch>()
//    private var startedStopwatch: Stopwatch? = null
//
//    private var nextId = 0
//    private var startTime = 0L

    private val pomodoroViewModel: PomodoroViewModel by lazy {
        ViewModelProvider(this).get(PomodoroViewModel::class.java)
    }
    private val stopwatchAdapter = StopwatchAdapter(this)

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

//        val handler = Handler(Looper.myLooper()!!)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        pomodoroViewModel.startTime = System.currentTimeMillis()

        lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                val time = System.currentTimeMillis()
                if (pomodoroViewModel.startedStopwatch != null && pomodoroViewModel.startedStopwatch!!.balanceMs > 1) {
                    pomodoroViewModel.startedStopwatch!!.balanceMs -= time - pomodoroViewModel.startTime
                        if (pomodoroViewModel.startedStopwatch!!.balanceMs < 1){
                            pomodoroViewModel.startedStopwatch!!.balanceMs = 1
                        }
//                        if (startedStopwatch!!.balanceMs <= 0 ) {startedStopwatch!!.isStarted = false}
//                        if (startedStopwatch!!.balanceMs <= 0 ) {handler.postAtFrontOfQueue {
//                            startedStopwatch!!.balanceMs = 0
////                            stop(startedStopwatch!!.id, startedStopwatch!!.startMs, startedStopwatch!!.balanceMs)
//                            stopwatchAdapter.submitList(stopwatches)
//                        }}
                    }
                pomodoroViewModel.startTime = time
                delay(INTERVAL)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        stopwatchAdapter.submitList(pomodoroViewModel.stopwatches.toList())
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
        pomodoroViewModel.stopwatches.remove(pomodoroViewModel.stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(pomodoroViewModel.stopwatches.toList())
    }

    //StopwatchListener
    override fun getStartedStopwatch(): Int {
        return pomodoroViewModel.startedStopwatch?.id ?: -1
    }

    //StopwatchListener
    override fun setStartedStopwatch(id: Int) {
        pomodoroViewModel.startedStopwatch = pomodoroViewModel.stopwatches.find { it.id == id }
    }

    private fun changeStopwatch(id: Int, startMs: Long, balanceMs: Long, isStarted: Boolean) {
        val newTimers = mutableListOf<Stopwatch>()
        pomodoroViewModel.stopwatches.forEach {
            if (it.id == id) {
                newTimers.add(Stopwatch(it.id, startMs, balanceMs, isStarted))
            } else {
                newTimers.add(it)
            }
        }
        stopwatchAdapter.submitList(newTimers)
        pomodoroViewModel.stopwatches.clear()
        pomodoroViewModel.stopwatches.addAll(newTimers)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d("TAG", "onAppBackgrounded(): notification was running")
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, pomodoroViewModel.startTime)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.d("TAG", "onAppForegrounded(): activity was running")
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }


    /////TimePickerFragment.TimePickerCallback
    override fun userChoice(timeMs: Long) {
        Log.d("classTimePickerFragment", "userChoice: time=$timeMs")
        if (timeMs > 0) {
            Log.d("classTimePickerFragment", "user: time=$timeMs")
            pomodoroViewModel.stopwatches.add(Stopwatch(pomodoroViewModel.nextId++, timeMs, timeMs, false))
            stopwatchAdapter.submitList(pomodoroViewModel.stopwatches.toList())
        }
    }


}