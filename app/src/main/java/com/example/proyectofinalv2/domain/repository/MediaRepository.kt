package com.example.proyectofinalv2.domain.repository

import com.example.proyectofinalv2.domain.model.Multimedia

interface MediaRepository {
    fun getMultimedia(): List<Multimedia>

    fun getMultimediasForNote(noteId:Long): List<Multimedia>

    suspend fun getMultimediaById(id:Long) : Multimedia?

    suspend fun updateMultimedia(media: Multimedia)

    suspend fun insertMultimedia(media: Multimedia)

    suspend fun deleteMultimedia(media: Multimedia)
}