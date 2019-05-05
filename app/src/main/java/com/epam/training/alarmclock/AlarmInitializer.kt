package com.epam.training.alarmclock

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.preference.PreferenceManager
import android.widget.Toast

fun setAlarm(context: Context?, alarmHour: Int, alarmMinute: Int, isFirstInit: Boolean) {
    val setAlarmIntent = Intent(context, AlarmBroadcastReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, setAlarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val calendar = if (isFirstInit) {
        calculateFirstAlarmAfterReboot(alarmHour, alarmMinute)
    } else {
        calculateAlarmToRepeat(context, alarmHour, alarmMinute)
    }

    with(context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager) {
        setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    setNotificationAlarm(context, calendar)
}

fun setAlarmFromSharedPreferences(context: Context?, isFirstInit: Boolean) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    if (sharedPreferences.contains(MainActivity.ALARM_TIME_HOURS)
        && sharedPreferences.contains(MainActivity.ALARM_TIME_MINUTES)
    ) {
        val alarmHour = sharedPreferences.getInt(MainActivity.ALARM_TIME_HOURS, 0)
        val alarmMinute = sharedPreferences.getInt(MainActivity.ALARM_TIME_MINUTES, 0)
        setAlarm(context, alarmHour, alarmMinute, isFirstInit)
    }
}

fun calculateFirstAlarmAfterReboot(alarmHour: Int, alarmMinute: Int): Calendar {
    val calendar = Calendar.getInstance()

    if (calendar.get(Calendar.HOUR_OF_DAY) >= alarmHour && calendar.get(Calendar.MINUTE) > alarmMinute) {
        calendar.add(Calendar.DATE, 1)
    }

    calendar.set(Calendar.HOUR_OF_DAY, alarmHour)
    calendar.set(Calendar.MINUTE, alarmMinute)

    return calendar
}

fun calculateAlarmToRepeat(context: Context?, alarmHour: Int, alarmMinute: Int): Calendar {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, 1)
    calendar.set(Calendar.HOUR_OF_DAY, alarmHour)
    calendar.set(Calendar.MINUTE, alarmMinute)
    Toast.makeText(context, context?.getString(R.string.next_alarm_tomorrow), Toast.LENGTH_LONG).show()

    return calendar
}

fun setNotificationAlarm(context: Context?, calendar: Calendar) {
    val setNotificationAlarmIntent = Intent(context, NotificationAlarmBroadcastReceiver::class.java)
        .putExtra(MainActivity.ALARM_TIME_HOURS, calendar.get(Calendar.HOUR_OF_DAY))
        .putExtra(MainActivity.ALARM_TIME_MINUTES, calendar.get(Calendar.MINUTE))
        .setAction(NotificationAlarmBroadcastReceiver.INTENT_ACTION_SHOW_NOTIFICATION)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, setNotificationAlarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    calendar.add(Calendar.MINUTE, -5)

    with(context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager) {
        setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}

fun cancelCurrentAlarm(context: Context?, alarmHour: Int, alarmMinute: Int) {
    Toast.makeText(context, R.string.alarm_canceled_toast_message, Toast.LENGTH_LONG).show()
    val alarmToCancelIntent = Intent(context, AlarmBroadcastReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, alarmToCancelIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    with(context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager) {
        cancel(pendingIntent)
    }
    //почему здесь не нужно context?. ?
    with(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
        cancel(NotificationAlarmBroadcastReceiver.NOTIFICATION_ID)
    }

    setAlarm(context, alarmHour, alarmMinute, false)
}