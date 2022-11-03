package com.example.proyectofinalv2.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "isTask") val isTask: Boolean?,
    @ColumnInfo(name = "dateCreation") val dateCreation: Date?,
    @ColumnInfo(name = "dateSetter") val dateSetter: Date?,
    @ColumnInfo(name = "media") val media: String?,
    @ColumnInfo(name = "author") val author: String?
)
