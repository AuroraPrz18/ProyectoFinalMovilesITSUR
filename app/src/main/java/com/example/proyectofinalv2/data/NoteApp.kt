package com.example.proyectofinalv2.data

import android.app.Application
import androidx.room.Room

class NoteApp: Application() {
    val room = Room.databaseBuilder(this,NoteDB::class.java, "notes").build()
}