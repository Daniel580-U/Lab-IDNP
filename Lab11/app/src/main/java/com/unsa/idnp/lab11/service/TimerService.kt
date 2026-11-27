package com.unsa.idnp.lab11.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.unsa.idnp.lab11.MainActivity
import com.unsa.idnp.lab11.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Servicio en primer plano que mantiene un temporizador activo y actualiza
 * periódicamente una notificación persistente.
 */
class TimerService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null
    private var elapsedSeconds = 0L

    companion object {
        const val CHANNEL_ID = "timer_foreground_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "ACTION_START_TIMER"
        const val ACTION_STOP = "ACTION_STOP_TIMER"
        private const val TAG = "TimerService"

        private val _elapsedFlow = MutableStateFlow(0L)
        val elapsedFlow = _elapsedFlow.asStateFlow()

        private val _isRunningFlow = MutableStateFlow(false)
        val isRunningFlow = _isRunningFlow.asStateFlow()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_STICKY
    }

    private fun startTimer() {
        if (_isRunningFlow.value) return
        elapsedSeconds = 0L
        _elapsedFlow.value = 0L
        _isRunningFlow.value = true

        Log.d(TAG, "Servicio iniciado: startForeground")
        startForeground(NOTIFICATION_ID, buildNotification(formatTime(elapsedSeconds)))

        timerJob = serviceScope.launch {
            while (isActive) {
                delay(1000L)
                elapsedSeconds++
                _elapsedFlow.value = elapsedSeconds
                val formatted = formatTime(elapsedSeconds)
                Log.d(TAG, "Tick temporizador: $formatted")
                val manager = getSystemService(NotificationManager::class.java)
                manager.notify(NOTIFICATION_ID, buildNotification(formatted))
            }
        }
    }

    private fun stopTimer() {
        Log.d(TAG, "Deteniendo servicio y cancelando actualizaciones")
        timerJob?.cancel()
        timerJob = null
        _isRunningFlow.value = false
        _elapsedFlow.value = 0L

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(timeText: String): Notification {
        val openAppIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, TimerService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Temporizador en ejecución")
            .setContentText("Tiempo transcurrido: $timeText")
            .setSmallIcon(R.drawable.ic_timer)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(openAppIntent)
            .addAction(R.drawable.ic_stop, "Detener", stopIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Temporizador Activo",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificación persistente del servicio de temporizador"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun formatTime(totalSeconds: Long): String {
        val hrs = totalSeconds / 3600
        val mins = (totalSeconds % 3600) / 60
        val secs = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hrs, mins, secs)
    }

    override fun onDestroy() {
        timerJob?.cancel()
        serviceScope.cancel()
        Log.d(TAG, "TimerService destruido")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
