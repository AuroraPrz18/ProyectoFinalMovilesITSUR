package com.example.proyectofinalv2.dao

import androidx.room.*
import com.example.proyectofinalv2.model.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note")
    fun getAll() : List<Note>

    @Query("SELECT * FROM Note WHERE id = :id")
    fun getById(id:Int) : Note

    @Update
    fun update(note: Note)

    @Insert
    fun insert(note: List<Note>)

    @Delete
    fun delete(note: Note)
}