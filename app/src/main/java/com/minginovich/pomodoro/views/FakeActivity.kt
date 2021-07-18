package com.minginovich.pomodoro.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FakeActivity: AppCompatActivity() {
//
//        private lateinit var binding: FakeActivityBinding
//        private var current = 0L
//
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//            binding = FakeActivityBinding.inflate(layoutInflater)
//            setContentView(binding.root)
//
//            binding.customViewOne.setPeriod(PERIOD)
//            binding.customViewTwo.setPeriod(PERIOD)
//
//            // Never use GlobalScope for real projects !!!
//            GlobalScope.launch {
//                while (current < PERIOD * REPEAT) {
//                    current += INTERVAL
//                    binding.customViewOne.setCurrent(current)
//                    binding.customViewTwo.setCurrent(current)
//                    delay(INTERVAL)
//                }
//            }
//        }
//
//        private companion object {
//
//            private const val INTERVAL = 100L
//            private const val PERIOD = 1000L * 30 // 30 sec
//            private const val REPEAT = 10 // 10 times
//        }
}