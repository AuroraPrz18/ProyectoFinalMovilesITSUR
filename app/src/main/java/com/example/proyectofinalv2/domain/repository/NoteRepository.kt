package com.example.proyectofinalv2.domain.repository

import com.example.proyectofinalv2.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): List<Note>

    suspend fun getNoteById(id: Long): Note?

    suspend fun insertNote(note: Note): Long

    suspend fun deleteNote(note : Note)
}