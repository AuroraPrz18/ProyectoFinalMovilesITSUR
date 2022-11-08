package com.example.proyectofinalv2.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.proyectofinalv2.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note;")
    fun getNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE isTask= :isTask ORDER BY dueDate ASC;")
    fun getTasksOnly(isTask: Boolean = true) : LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE isTask= :isTask ORDER BY dateCreation DESC;")
    fun getNotesOnly(isTask: Boolean = false) : LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE title LIKE '%'+ :search +'%' OR description LIKE '%'+ :search +'%';")
    suspend fun getSearch(search: String) : List<Note>

    @Query("SELECT * FROM Note WHERE id = :id")
    suspend fun getNoteById(id:Int) : Note?

    @Update
    suspend fun updateNote(note: Note)

    @Insert
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}