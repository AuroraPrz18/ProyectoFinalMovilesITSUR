package com.example.proyectofinalv2.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "noteId") var noteId: Long,
    @ColumnInfo(name = "date") val date: Date?,
    @ColumnInfo(name = "isSetUp") var isSetUp: Boolean?
) : java.io.Serializable
