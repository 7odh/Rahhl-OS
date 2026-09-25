package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.util.NotificationHelper

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "تذكير إنجاز اليوم"
        val message = intent.getStringExtra("message") ?: "لا تنسَ متابعة عاداتك ومهامك اليومية!"
        NotificationHelper.showNotification(
            context = context,
            id = (System.currentTimeMillis() % 100000).toInt(),
            channelId = NotificationHelper.CHANNEL_REMINDERS,
            title = title,
            content = message
        )
    }
}
