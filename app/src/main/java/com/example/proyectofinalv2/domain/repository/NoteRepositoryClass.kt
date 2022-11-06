package com.example.proyectofinalv2.domain.repository

import androidx.annotation.WorkerThread
import com.example.proyectofinalv2.data.dao.NoteDao
import com.example.proyectofinalv2.domain.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepositoryClass(private val  noteDao: NoteDao){
    fun allNotes() = noteDao.getNotes()
    suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.insertNote(note)
    suspend fun getNoteById(id: Int): Note?{
        return noteDao.getNoteById(id)
    }
}