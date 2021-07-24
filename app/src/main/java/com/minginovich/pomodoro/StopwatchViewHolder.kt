package com.minginovich.pomodoro

import android.annotation.SuppressLint
import com.minginovich.pomodoro.databinding.StopwatchItemBinding
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.util.Log
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
            if (stopwatch.balanceMs == 1L) {
                Log.d("stopwatch", "finishTimer(${stopwatch.id}, ${stopwatch.balanceMs.displayTime()})")
//                listener.setStartedStopwatch(-1)
                finishTimer(stopwatch)
            } else{
                Log.d("stopwatch", "startTimer(${stopwatch.id}, ${stopwatch.balanceMs.displayTime()})")
                startTimer(stopwatch)
//                stopTimer(stopwatch)
            }
        } else {
            if (stopwatch.balanceMs == 1L) {
                Log.d("stopwatch", "finishTimer(${stopwatch.id}, ${stopwatch.balanceMs.displayTime()})")
//                listener.setStartedStopwatch(-1)
                finishTimer(stopwatch)
            } else{
                Log.d("stopwatch", "stopTimer(${stopwatch.id}, ${stopwatch.balanceMs.displayTime()})")
                stopTimer(stopwatch)
            }
        }

        initButtonsListeners(stopwatch)
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startPauseButton.setOnClickListener {
            if (listener.getStartedStopwatch() == stopwatch.id) {
                listener.stop(stopwatch.id,stopwatch.startMs, stopwatch.balanceMs)
                listener.setStartedStopwatch(-1)
            } else {
                listener.start(stopwatch.id,stopwatch.startMs, stopwatch.balanceMs)
                listener.setStartedStopwatch(stopwatch.id)
            }
        }

        binding.deleteButton.setOnClickListener {
            if (listener.getStartedStopwatch() == stopwatch.id){
                listener.setStartedStopwatch(-1)
            }
            listener.delete(stopwatch.id)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun startTimer(stopwatch: Stopwatch) {
        val drawable = resources.getDrawable(R.drawable.ic_baseline_pause_24)
        binding.startPauseButton.text = "Pause"
        binding.startPauseButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        binding.constraintLayout.setBackgroundColor(Color.WHITE)
        binding.startPauseButton.isActivated = true
        binding.startPauseButton.isClickable = true
        binding.startPauseButton.isEnabled = true
        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        val drawable = resources.getDrawable(R.drawable.ic_baseline_play_arrow_24)
        binding.startPauseButton.text = "Start"
        binding.startPauseButton.isActivated = true
        binding.startPauseButton.isClickable = true
        binding.startPauseButton.isEnabled = true
        binding.startPauseButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        binding.constraintLayout.setBackgroundColor(Color.WHITE)
        timer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    @SuppressLint("ResourceAsColor")
    private fun finishTimer(stopwatch: Stopwatch){
        binding.startPauseButton.text = "Finish"
        binding.startPauseButton.isActivated = false
        binding.startPauseButton.isClickable = false
        binding.startPauseButton.isEnabled = false
        binding.constraintLayout.setBackgroundColor(Color.RED)
        binding.startPauseButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        timer?.cancel()
        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(PERIOD, INTERVAL) {

            override fun onTick(millisUntilFinished: Long) {
//                stopwatch.balanceMs -= interval
                binding.stopwatchTimer.text = stopwatch.balanceMs.displayTime()
                ////////////////////////////////////////
                binding.customViewProgress.setCurrent(stopwatch.startMs - stopwatch.balanceMs)
                if (listener.getStartedStopwatch() == stopwatch.id && stopwatch.balanceMs  <= 1){
                    listener.stop(stopwatch.id, stopwatch.startMs, stopwatch.balanceMs)
                }
                if (listener.getStartedStopwatch() != stopwatch.id){
                    listener.stop(stopwatch.id, stopwatch.startMs, stopwatch.balanceMs)
                }
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.balanceMs.displayTime()
            }
        }
    }

}