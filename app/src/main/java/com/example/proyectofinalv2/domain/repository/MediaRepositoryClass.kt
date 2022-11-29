package com.example.proyectofinalv2.domain.repository

import com.example.proyectofinalv2.data.dao.MediaDAO
import com.example.proyectofinalv2.domain.model.Multimedia


class MediaRepositoryClass(private val  mediaDao: MediaDAO) {
    fun allMedias() = mediaDao.getMultimedia();
    fun onlyMediaForNote(id: Long) = mediaDao.getMultimediasForNote(id);
    suspend fun getMediaById(id: Long): Multimedia? {
        return mediaDao.getMultimediaById(id);
    }
    suspend fun insertMultimedia(media: Multimedia) = mediaDao.insertMultimedia(media);
    suspend fun updateMultimedia(media: Multimedia) = mediaDao.updateMultimedia(media);
    suspend fun deleteMultimedia(media: Multimedia) = mediaDao.deleteMultimedia(media);
}