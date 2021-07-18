package com.minginovich.pomodoro

import com.minginovich.pomodoro.databinding.StopwatchItemBinding
import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.minginovich.pomodoro.views.FakeActivity


class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.balanceMs.displayTime()
        //////////////////////////////////////////
        binding.customViewProgress.setPeriod(stopwatch.startMs)
        binding.customViewProgress.setCurrent(stopwatch.startMs - stopwatch.balanceMs)

        if (listener.getStartedStopwatch() == stopwatch.id){
            startTimer(stopwatch)
        } else {
            stopTimer(stopwatch)
        }

        initButtonsListeners(stopwatch)
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startPauseButton.setOnClickListener {
            if (listener.getStartedStopwatch() == stopwatch.id) {
                listener.stop(stopwatch.id,stopwatch.startMs, stopwatch.balanceMs)
                listener.setStartedStopwatch(-1)
            } else {
                listener.setStartedStopwatch(stopwatch.id)
                listener.start(stopwatch.id,stopwatch.startMs, stopwatch.balanceMs)
            }
        }

        binding.deleteButton.setOnClickListener {
            if (listener.getStartedStopwatch() == stopwatch.id){
                listener.setStartedStopwatch(-1)
            }
            listener.delete(stopwatch.id)
        }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        val drawable = resources.getDrawable(R.drawable.ic_baseline_pause_24)
        binding.startPauseButton.text = "Pause"
        binding.startPauseButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        val drawable = resources.getDrawable(R.drawable.ic_baseline_play_arrow_24)
        binding.startPauseButton.text = "Start"
        binding.startPauseButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        timer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                stopwatch.balanceMs -= interval
                binding.stopwatchTimer.text = stopwatch.balanceMs.displayTime()
                //////////////////////////////////////////
                binding.customViewProgress.setCurrent(stopwatch.startMs - stopwatch.balanceMs)
                if (listener.getStartedStopwatch() != stopwatch.id){
                    listener.stop(stopwatch.id, stopwatch.startMs, stopwatch.balanceMs)
                }
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.balanceMs.displayTime()
            }
        }
    }

    private fun Long.displayTime(): String {
        if (this <= 0L) {
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60
        val ms = this % 1000 / 10

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}:${displaySlot(ms)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {

        private const val START_TIME = "00:00:00:00"
        private const val UNIT_TEN_MS = 10L
        private const val PERIOD = 1000L * 60L * 60L * 24L // Day
    }
}