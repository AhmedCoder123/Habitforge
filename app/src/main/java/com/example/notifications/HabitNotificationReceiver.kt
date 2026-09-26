package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HabitNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val manager = HabitNotificationManager(context)
        if (!manager.isNotificationEnabled()) return

        val title = intent?.getStringExtra("extra_title") ?: "Habit Check-in"
        val message = intent?.getStringExtra("extra_message") ?: "Time to check off your daily habits!"
        val isReset = intent?.getBooleanExtra("extra_is_reset", false) ?: false

        manager.triggerTestNotification(title, message, isResetChannel = isReset)
    }
}
