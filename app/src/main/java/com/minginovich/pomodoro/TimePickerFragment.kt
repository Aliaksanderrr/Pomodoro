package com.minginovich.pomodoro

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    interface TimePickerCallback{
        fun userChoice(timeMs: Long)
    }

    private lateinit var listener: TimePickerCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (context is TimePickerCallback){
            listener = context as TimePickerCallback
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //defolt value
        val hour = 0
        val minute = 1

        // Create a new instance of TimePickerDialog and return it
//        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
        return TimePickerDialog(activity, this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        Log.d("classTimePickerFragment", "user: hour= $hourOfDay, min=$minute")
        val millis: Long = (hourOfDay * 60 * 60 * 1000 + minute * 60 * 1000).toLong()
        listener.userChoice(millis)
    }
}