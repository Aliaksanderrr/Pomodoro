package com.minginovich.pomodoro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class ForegroundService : Service() {

    private var isServiceStarted = false
    private var notificationManager: NotificationManager? = null

    private var startTime: Long? = null
    private var stopwatchId: Int? = null
    private var stopwatchStartMS: Long? = null
    private var stopwatchBalanceMS: Long? = null
    private var job: Job? = null

    private val builder by lazy {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pomodoro")
            .setGroup("Timer")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processCommand(intent)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun processCommand(intent: Intent?) {
        Log.d("TAG", "processCommand(intent: Intent?)")
        when (intent?.extras?.getString(COMMAND_ID) ?: INVALID) {
            COMMAND_START -> {
                startTime = intent?.extras?.getLong(STARTED_TIMER_TIME_MS) ?: return
                stopwatchId = intent.extras?.getInt(STARTED_STOPWATCH_ID) ?: return
                stopwatchStartMS = intent.extras?.getLong(STARTED_STOPWATCH_START_MS) ?: return
                stopwatchBalanceMS = intent.extras?.getLong(STARTED_STOPWATCH_BALANCE_MS) ?: return
                Log.d("TAG", "processCommand: val startTime = $startTime")
                commandStart()
            }
            COMMAND_STOP -> {
                commandStop()
                Log.d("TAG", "processCommand: COMMAND_STOP -> commandStop()")
            }
            INVALID -> return
        }
    }

    private fun commandStart() {
        if (isServiceStarted) {
            return
        }
        Log.d("TAG", "commandStart()")
        try {
            moveToStartedState()
            startForegroundAndShowNotification()
            continueTimer()
        } finally {
            isServiceStarted = true
        }
    }

    private fun continueTimer() {
        job = GlobalScope.launch(Dispatchers.Main) {
            while (true) {

                val time = System.currentTimeMillis()
                    stopwatchBalanceMS = stopwatchBalanceMS?.minus(time - startTime!!)
                    if (stopwatchBalanceMS!! < 1){
                        stopwatchBalanceMS = 1
                    }
                startTime = time
                if (stopwatchBalanceMS == 1L){
                    notificationManager?.notify(
                        NOTIFICATION_ID,
                        getNotification(
                            stopwatchBalanceMS!!.displayTime() + ": timer finished"
                        )
                    )

                } else {
                    notificationManager?.notify(
                        NOTIFICATION_ID,
                        getNotification(
                            stopwatchBalanceMS!!.displayTime()
                        )
                    )
                }
                delay(INTERVAL)
            }
        }
    }

    private fun commandStop() {
        if (!isServiceStarted) {
            return
        }
        Log.i("TAG", "commandStop()")
        try {
            job?.cancel()
            stopForeground(true)
            stopSelf()
        } finally {
            isServiceStarted = false
        }
    }

    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("TAG", "moveToStartedState(): Running on Android O or higher")
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            Log.d("TAG", "moveToStartedState(): Running on Android N or lower")
            startService(Intent(this, ForegroundService::class.java))
        }
    }

    private fun startForegroundAndShowNotification() {
        createChannel()
        val notification = getNotification("content")
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun getNotification(content: String) = builder.setContentText(content).build()


    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "pomodoro"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, channelName, importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }

    private companion object {

        private const val CHANNEL_ID = "Channel_ID22"
        private const val NOTIFICATION_ID = 8888
        private const val INTERVAL = 1000L
    }
}