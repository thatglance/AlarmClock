package com.epam.training.alarmclock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.preference.PreferenceManager
import android.widget.TextView
import android.widget.TimePicker

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        val textView = findViewById<TextView>(R.id.textView)
        val chooseTimeButton = findViewById<Button>(R.id.buttonSetTime)
        chooseTimeButton.setOnClickListener {
            saveAlarmTime(timePicker.hour, timePicker.minute)
            setAlarm(this, timePicker.hour, timePicker.minute, true)
            showCurrentAlarmTime(textView, timePicker.hour, timePicker.minute)
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.contains(ALARM_TIME_HOURS)
            && sharedPreferences.contains(ALARM_TIME_MINUTES)
        ) {
            val alarmHour = sharedPreferences.getInt(ALARM_TIME_HOURS, -1)
            val alarmMinute = sharedPreferences.getInt(ALARM_TIME_MINUTES, -1)
            showCurrentAlarmTime(textView, alarmHour, alarmMinute)
        }
    }

    private fun showCurrentAlarmTime(textView: TextView, alarmHour: Int, alarmMinute: Int) {
        textView.text = getString(R.string.label_alarm_set, timeToString(alarmHour), timeToString(alarmMinute))
    }

    private fun saveAlarmTime(alarmHour: Int, alarmMinute: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPreferences.edit()
        editor
            .putInt(ALARM_TIME_HOURS, alarmHour)
            .putInt(ALARM_TIME_MINUTES, alarmMinute)
        editor.apply()
    }

    companion object {
        const val ALARM_TIME_HOURS = "ALARM_TIME_HOURS"
        const val ALARM_TIME_MINUTES = "ALARM_TIME_MINUTES"
        fun timeToString(timeAmount: Int) = if (timeAmount in 0..9) "0$timeAmount" else timeAmount.toString()
    }
}
