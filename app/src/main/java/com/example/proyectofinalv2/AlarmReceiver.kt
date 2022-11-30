package com.example.proyectofinalv2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { createNotificationChannel(it, null) }
        when (intent?.action ) {
            "android.intent.action.BOOT_COMPLETED" -> {
                Log.d("SEBOOTEO", "SE HA CARGADO ANDROID");
                context?.let { intent?.let {
                        it1 -> mostrarNotificacion(it, it1) } };
            }
            else -> {
                context?.let { intent?.let {
                        it1 -> mostrarNotificacion(it, it1) } };
            }
        }
    }

    private fun mostrarNotificacion(context: Context, intent2: Intent) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP //Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("id", 1002)
        var pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_MUTABLE)


        val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_add_alarm_24)
            .setContentTitle("Hay una nota pendiente")
            .setContentText("checala, va?")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, builder.build())
    }

    companion object {
        val CHANNEL_ID = "NOTES_S19120121"
        fun createNotificationChannel(context: Context, intent: Intent?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name: CharSequence = context.getString(R.string.channel_name)
                val description: String = context.getString(R.string.channel_desc)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance)
                channel.description = description
                val notificationManager: NotificationManager = context.getSystemService(
                    NotificationManager::class.java
                )
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

}