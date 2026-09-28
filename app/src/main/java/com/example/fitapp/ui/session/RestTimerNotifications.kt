package com.example.fitapp.ui.session

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.fitapp.MainActivity
import com.example.fitapp.R

object RestTimerNotifications {
    const val ACTION_REST_TIMER_FINISHED = "com.example.fitapp.action.REST_TIMER_FINISHED"

    var isForeground: Boolean = false
        internal set

    private const val CHANNEL_ID = "rest_timer"
    private const val NOTIFICATION_ID = 1001
    private const val ALARM_REQUEST_CODE = 2001

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Таймер отдыха",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Уведомления об окончании отдыха между подходами"
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun scheduleFinishedNotification(context: Context, triggerAtMillis: Long): Boolean {
        createChannel(context)
        context.getSharedPreferences("rest_timer", Context.MODE_PRIVATE).edit()
            .putLong("deadline", triggerAtMillis).apply()
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = alarmIntent(context, triggerAtMillis)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || manager.canScheduleExactAlarms()) {
            try {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, intent)
                return true
            } catch (_: SecurityException) { /* permission may be revoked concurrently */ }
        }
        manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, intent)
        return false
    }

    // Receiver and foreground timer share the deadline claim: exactly one alert per timer.
    @Synchronized
    private fun claim(context: Context, deadline: Long): Boolean {
        val prefs = context.getSharedPreferences("rest_timer", Context.MODE_PRIVATE)
        if (deadline <= 0 || prefs.getLong("deadline", 0) != deadline || prefs.getLong("alerted", 0) == deadline) return false
        prefs.edit().putLong("alerted", deadline).apply()
        return true
    }

    fun signalForeground(context: Context, deadline: Long) {
        if (!claim(context, deadline)) return
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(alarmIntent(context))
        try {
            val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 400)
            Handler(Looper.getMainLooper()).postDelayed({ tone.release() }, 600)
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE))
            else @Suppress("DEPRECATION") vibrator.vibrate(350)
        } catch (_: RuntimeException) { /* silent / restricted devices retain the visible timer */ }
    }

    fun openSignalSettings(context: Context) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = if (!NotificationManagerCompat.from(context).areNotificationsEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) {
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}"))
        } else Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
        try { context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        catch (_: android.content.ActivityNotFoundException) {
            context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    fun backgroundNotice(context: Context, exact: Boolean): String? {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled())
            return "Уведомления выключены: сигнал доступен только на открытом экране тренировки. Разрешите уведомления в настройках приложения."
        return if (exact) "При энергосбережении телефона фоновый сигнал может задержаться." else "В фоне сигнал может задержаться. Для своевременного сигнала разрешите точные будильники в настройках приложения."
    }

    fun cancelFinishedNotification(context: Context) {
        context.getSharedPreferences("rest_timer", Context.MODE_PRIVATE).edit().remove("deadline").apply()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmIntent(context))
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    fun showFinishedNotification(context: Context, deadline: Long) {
        if (isForeground) { signalForeground(context, deadline); return }
        // Do not consume the foreground alert when notification permission is denied.
        createChannel(context)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return
        if (!claim(context, deadline)) return
        val openAppIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_rest_timer)
            .setContentTitle("Отдых закончен")
            .setContentText("Пора начинать следующий подход")
            .setContentIntent(openAppIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun alarmIntent(context: Context, deadline: Long = 0): PendingIntent {
        val intent = Intent(context, RestTimerAlarmReceiver::class.java).apply {
            action = ACTION_REST_TIMER_FINISHED
            putExtra("deadline", deadline)
        }
        return PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
