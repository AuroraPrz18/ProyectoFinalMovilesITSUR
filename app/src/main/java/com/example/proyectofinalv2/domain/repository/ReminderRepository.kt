package com.example.proyectofinalv2.domain.repository

import com.example.proyectofinalv2.domain.model.Reminder


interface ReminderRepository {
    fun getReminders(): List<Reminder>

    fun getRemindersForNote(noteId:Long): List<Reminder>

    suspend fun getReminderById(id:Long) : Reminder?

    suspend fun updateReminder(reminder: Reminder)

    suspend fun insertReminder(reminder: Reminder)

    suspend fun deleteReminder(reminder: Reminder)
}