package com.example.proyectofinalv2.data.dao

import androidx.room.*
import com.example.proyectofinalv2.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note")
    fun getNotes() : List<Note>

    @Query("SELECT * FROM Note WHERE id = :id")
    fun getNoteById(id:Int) : Note?

    //TODO: Queries to get only task or only notes

    @Update
    fun updateNote(note: Note)

    @Insert
    fun insertNote(note: Note)

    @Delete
    fun deleteNote(note: Note)
}