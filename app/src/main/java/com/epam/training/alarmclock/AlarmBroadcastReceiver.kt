package com.epam.training.alarmclock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.widget.Toast
import androidx.core.app.NotificationCompat

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, R.string.alarm_message, Toast.LENGTH_LONG).show()
        val mediaPlayer = MediaPlayer.create(context, R.raw.whistle_sound)
        mediaPlayer?.start()

        with(context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
            cancel(NotificationAlarmBroadcastReceiver.NOTIFICATION_ID)
        }

        setAlarmFromSharedPreferences(context, false)
    }

}

class NotificationAlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, R.string.notification_alarm_toast_message, Toast.LENGTH_LONG).show()

        intent?.let {
            val alarmHour = it.getIntExtra(MainActivity.ALARM_TIME_HOURS, -1)
            val alarmMinute = it.getIntExtra(MainActivity.ALARM_TIME_MINUTES, -1)

            when (it.action) {
                INTENT_ACTION_SHOW_NOTIFICATION -> showNotification(context, alarmHour, alarmMinute)
                INTENT_ACTION_CANCEL_CURRENT_ALARM -> cancelCurrentAlarm(context, alarmHour, alarmMinute)
            }
        }
    }

    private fun showNotification(context: Context?, alarmHour: Int, alarmMinute: Int) {
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingTapIntent = PendingIntent.getActivity(context, 0, tapIntent, 0)

        val cancelIntent = Intent(context, NotificationAlarmBroadcastReceiver::class.java)
            .setAction(INTENT_ACTION_CANCEL_CURRENT_ALARM)
        val pendingCancelIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, 0)

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE)
        notificationManager.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(CHANNEL_NAME)
            .setContentText(
                context.resources.getString(
                    R.string.notification_alarm_text,
                    MainActivity.timeToString(alarmHour),
                    MainActivity.timeToString(alarmMinute)
                )
            )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingTapIntent)
            .addAction(R.mipmap.ic_launcher, context.resources.getString(R.string.cancel), pendingCancelIntent)
        val notification = notificationBuilder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "ALARM_NOTIFICATION_CHANNEL_ID"
        private const val CHANNEL_NAME = "ALARM_NOTIFICATION_CHANNEL"
        const val NOTIFICATION_ID = 0

        const val INTENT_ACTION_SHOW_NOTIFICATION = "com.epam.training.alarmclock.SHOW_NOTIFICATION"
        const val INTENT_ACTION_CANCEL_CURRENT_ALARM = "com.epam.training.alarmclock.CANCEL_CURRENT_ALARM"
    }
}
