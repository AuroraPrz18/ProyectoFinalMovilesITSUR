package com.example.proyectofinalv2.domain.repository

import com.example.proyectofinalv2.data.dao.ReminderDAO
import com.example.proyectofinalv2.domain.model.Reminder

class ReminderRepositoryClass(private val  reminderDao: ReminderDAO){
    fun allReminders() = reminderDao.getReminders();
    fun getRemindersForNote(noteId:Long) = reminderDao.getRemindersForNote(noteId);
    suspend fun getReminderById(id:Long): Reminder? {
        return reminderDao.getReminderById(id);
    }
    suspend fun insertReminder(reminder: Reminder) = reminderDao.insertReminder(reminder);
    suspend fun updateReminder(reminder: Reminder) = reminderDao.updateReminder(reminder);
    suspend fun deleteReminder(reminder: Reminder) = reminderDao.deleteReminder(reminder);

}