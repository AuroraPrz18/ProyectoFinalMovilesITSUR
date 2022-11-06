package com.example.proyectofinalv2.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.*

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "isTask") val isTask: Boolean?,
    @ColumnInfo(name = "dateCreation") val dateCreation: Date?,
    @ColumnInfo(name = "dueDate") val dueDate: Date?,
    @ColumnInfo(name = "isCompleted") val isComplete: Boolean?,
    @ColumnInfo(name = "dateCompleted") val dateCompleted: Date?
) : java.io.Serializable
