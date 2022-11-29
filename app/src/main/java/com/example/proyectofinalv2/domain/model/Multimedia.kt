package com.example.proyectofinalv2.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class Multimedia (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "noteId") var noteId: Long,
    @ColumnInfo(name = "type") val type: Long,
    @ColumnInfo(name = "path") val path: String
)  : java.io.Serializable