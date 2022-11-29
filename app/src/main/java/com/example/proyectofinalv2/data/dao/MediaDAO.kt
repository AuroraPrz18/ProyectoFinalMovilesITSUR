package com.example.proyectofinalv2.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.proyectofinalv2.domain.model.Multimedia
@Dao
interface MediaDAO {
    @Query("SELECT * FROM Multimedia;")
    fun getMultimedia() : LiveData<List<Multimedia>>

    @Query("SELECT * FROM Multimedia WHERE noteId = :noteId;")
    fun getMultimediasForNote(noteId:Long) : LiveData<List<Multimedia>>

    @Query("SELECT * FROM Multimedia WHERE id = :id;")
    suspend fun getMultimediaById(id:Long) : Multimedia?

    @Update
    suspend fun updateMultimedia(media: Multimedia)

    @Insert
    suspend fun insertMultimedia(media: Multimedia)

    @Delete
    suspend fun deleteMultimedia(media: Multimedia)
}