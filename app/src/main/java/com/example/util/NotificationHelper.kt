package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    const val CHANNEL_REMINDERS = "achievement_reminders"
    const val CHANNEL_FOCUS = "achievement_focus"
    const val CHANNEL_MOTIVATION = "achievement_motivation"

    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val chReminders = NotificationChannel(
                CHANNEL_REMINDERS,
                "تنبيهات العادات والمهام",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "تنبيهات مواعيد العادات والمهام اليومية"
                enableVibration(true)
            }

            val chFocus = NotificationChannel(
                CHANNEL_FOCUS,
                "جلسات التركيز",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات اكتمال جلسات Pomodoro والتركيز"
                enableVibration(true)
            }

            val chMotivation = NotificationChannel(
                CHANNEL_MOTIVATION,
                "التحفيز ومراجعة اليوم",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "رسائل تحفيزية وتذكير مراجعة اليوم"
            }

            manager.createNotificationChannel(chReminders)
            manager.createNotificationChannel(chFocus)
            manager.createNotificationChannel(chMotivation)
        }
    }

    fun showNotification(
        context: Context,
        id: Int,
        channelId: String,
        title: String,
        content: String
    ) {
        initChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(id, builder.build())
        } catch (_: SecurityException) {
            // Permission not yet granted
        }
    }

    fun showFocusFinishedNotification(context: Context, sessionTitle: String, durationMinutes: Int) {
        showNotification(
            context = context,
            id = 1001,
            channelId = CHANNEL_FOCUS,
            title = "🎉 اكتملت جلسة التركيز!",
            content = "أحسنت! أتممت $durationMinutes دقيقة تركيز في: $sessionTitle"
        )
    }

    fun showHabitReminderNotification(context: Context, habitName: String) {
        showNotification(
            context = context,
            id = (System.currentTimeMillis() % 100000).toInt(),
            channelId = CHANNEL_REMINDERS,
            title = "⏰ حان وقت العادة: $habitName",
            content = "خطوة صغيرة الآن تصنع فارقاً كبيراً في إنجازك اليوم."
        )
    }

    fun showDailyReviewNotification(context: Context, quoteText: String) {
        showNotification(
            context = context,
            id = 2001,
            channelId = CHANNEL_MOTIVATION,
            title = "🌙 كيف كان إنجازك اليوم؟",
            content = "$quoteText — سجل مراجعتك لليوم واحتفل بتقدمك."
        )
    }
}
