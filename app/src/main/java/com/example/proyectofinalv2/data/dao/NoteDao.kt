package com.example.proyectofinalv2.data.dao

import androidx.room.*
import com.example.proyectofinalv2.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note")
    fun getNotes() : Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE id = :id")
    suspend fun getNoteById(id:Int) : Note?

    @Update
    suspend fun updateNote(note: Note)

    @Insert
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}