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
import com.example.proyectofinalv2.data.NoteApp
import com.example.proyectofinalv2.data.NoteDB
import com.example.proyectofinalv2.domain.model.Multimedia
import com.example.proyectofinalv2.domain.model.Note
import com.example.proyectofinalv2.ui.main.DetailsActivity
import java.time.LocalDate
import java.util.*
import kotlin.math.log

const val notiId = "id"
const val title = "title"
const val desc = "desc"

class AlarmReceiver: BroadcastReceiver() {
    var notes = ArrayList<Note>()
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { createNotificationChannel(it, null) }

        val bd = (context?.applicationContext  as NoteApp).database
        val noteDao = (context?.applicationContext  as NoteApp ).database?.noteDao()
        val notasData = noteDao?.getNotes()

        notasData?.observeForever {
            notes = it as ArrayList<Note>
        }
        mostrarNotificacion(context, intent);
    }

    private fun mostrarNotificacion(context: Context?, intent: Intent?) {
        val note: Note = intent?.getSerializableExtra("note") as Note
        val intentTap = Intent(context, DetailsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("note", note)
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            context, 0, intentTap,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_add_alarm_24)
            .setContentTitle(intent?.getStringExtra(title))
            .setContentText(intent?.getStringExtra(desc))
            .setContentIntent(notifyPendingIntent)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var id: Int = intent?.getIntExtra("notiId", 0)!!
        notificationManager.notify(id, builder.build())
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