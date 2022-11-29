package com.example.proyectofinalv2.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "isTask") var isTask: Boolean?,
    @ColumnInfo(name = "dateCreation") val dateCreation: Date?,
    @ColumnInfo(name = "dueDate") var dueDate: Date?,
    @ColumnInfo(name = "isCompleted") var isComplete: Boolean?,
    @ColumnInfo(name = "dateCompleted") val dateCompleted: Date?
) : java.io.Serializable
