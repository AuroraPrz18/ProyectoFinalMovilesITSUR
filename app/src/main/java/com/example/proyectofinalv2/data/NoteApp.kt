package com.example.proyectofinalv2.data

import android.app.Application
import androidx.room.Room
import com.example.proyectofinalv2.domain.repository.NoteRepositoryClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NoteApp: Application() {
    val database: NoteDB? by lazy { NoteDB.invoke(this) }
}