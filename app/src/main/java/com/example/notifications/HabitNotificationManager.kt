package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class HabitNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_HABITS_ID = "habit_reminders_channel"
        const val CHANNEL_RESET_ID = "karachi_reset_channel"
        const val NOTIFICATION_ID_TEST = 1001
        const val NOTIFICATION_ID_RESET = 1002
        const val NOTIFICATION_ID_ROUTINE = 1003

        const val PREFS_NAME = "habit_notification_prefs"
        const val KEY_NOTIF_ENABLED = "key_notif_enabled"
        const val KEY_MIDNIGHT_ALERT = "key_midnight_alert"
        const val KEY_MORNING_BRIEF = "key_morning_brief"
        const val KEY_WEEKEND_RUN = "key_weekend_run"
        const val KEY_GROOMING_ALERT = "key_grooming_alert"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val habitChannel = NotificationChannel(
                CHANNEL_HABITS_ID,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily habit schedule reminders, hydration, and gym alerts"
                enableVibration(true)
            }

            val resetChannel = NotificationChannel(
                CHANNEL_RESET_ID,
                "Karachi Daily Reset",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Strict 12:00 AM Karachi Time (GMT+5) daily reset warning and status"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(habitChannel)
            notificationManager.createNotificationChannel(resetChannel)
        }
    }

    fun isNotificationEnabled(): Boolean = prefs.getBoolean(KEY_NOTIF_ENABLED, true)
    fun setNotificationEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_NOTIF_ENABLED, enabled).apply()

    fun isMidnightAlertEnabled(): Boolean = prefs.getBoolean(KEY_MIDNIGHT_ALERT, true)
    fun setMidnightAlertEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_MIDNIGHT_ALERT, enabled).apply()

    fun isMorningBriefEnabled(): Boolean = prefs.getBoolean(KEY_MORNING_BRIEF, true)
    fun setMorningBriefEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_MORNING_BRIEF, enabled).apply()

    fun isWeekendRunEnabled(): Boolean = prefs.getBoolean(KEY_WEEKEND_RUN, true)
    fun setWeekendRunEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_WEEKEND_RUN, enabled).apply()

    fun isGroomingAlertEnabled(): Boolean = prefs.getBoolean(KEY_GROOMING_ALERT, true)
    fun setGroomingAlertEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_GROOMING_ALERT, enabled).apply()

    fun triggerTestNotification(title: String, message: String, isResetChannel: Boolean = false) {
        val channelId = if (isResetChannel) CHANNEL_RESET_ID else CHANNEL_HABITS_ID
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(if (isResetChannel) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notifId = if (isResetChannel) NOTIFICATION_ID_RESET else NOTIFICATION_ID_TEST
        notificationManager.notify(notifId, notification)
    }

    fun showMidnightResetWarningNotification(remainingMinutes: Int) {
        triggerTestNotification(
            title = "12:00 AM Karachi Reset Warning",
            message = "Only $remainingMinutes minutes remaining before today's Karachi checklist resets! Complete your remaining habits.",
            isResetChannel = true
        )
    }

    fun showMorningBriefNotification(scheduledHabitsCount: Int) {
        triggerTestNotification(
            title = "Good Morning (Karachi GMT+5)",
            message = "You have $scheduledHabitsCount habits on your schedule today. Stay disciplined and build momentum!",
            isResetChannel = false
        )
    }
}
