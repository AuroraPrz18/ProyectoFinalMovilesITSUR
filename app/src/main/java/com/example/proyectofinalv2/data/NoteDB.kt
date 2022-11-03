package com.example.proyectofinalv2.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyectofinalv2.dao.NoteDao
import com.example.proyectofinalv2.model.Note

@Database(
    entities = [Note::class],
    version = 1
)
abstract class NoteDB : RoomDatabase() {
    abstract fun noteDao():NoteDao
}