package com.epam.training.alarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RebootBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_REBOOT -> setAlarmFromSharedPreferences(context, true)
        }
    }

}