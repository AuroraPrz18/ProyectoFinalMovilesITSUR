package com.example.proyectofinalv2.data

import android.app.Application

class NoteApp: Application() {
    val database: NoteDB? by lazy { NoteDB.invoke(this) }
}