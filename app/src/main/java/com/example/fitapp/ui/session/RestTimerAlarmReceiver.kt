package com.example.fitapp.ui.session

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RestTimerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == RestTimerNotifications.ACTION_REST_TIMER_FINISHED) {
            RestTimerNotifications.showFinishedNotification(context)
        }
    }
}
