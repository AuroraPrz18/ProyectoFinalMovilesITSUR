package com.example.proyectofinalv2.data.dao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.proyectofinalv2.domain.model.Reminder

@Dao
interface ReminderDAO {
    @Query("SELECT * FROM Reminder;")
    fun getReminders() : List<Reminder>

    @Query("SELECT * FROM Reminder WHERE noteId = :noteId;")
    fun getRemindersForNote(noteId:Long) : List<Reminder>

    @Query("SELECT * FROM Reminder WHERE id = :id;")
    suspend fun getReminderById(id:Long) : Reminder?

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Insert
    suspend fun insertReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)
}