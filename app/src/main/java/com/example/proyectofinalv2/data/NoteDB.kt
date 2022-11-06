package com.example.proyectofinalv2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.proyectofinalv2.data.dao.NoteDao
import com.example.proyectofinalv2.domain.Converters.Converters
import com.example.proyectofinalv2.domain.model.Note

@Database(
    entities =[Note::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class NoteDB : RoomDatabase() {
    abstract fun noteDao():NoteDao

    // Singleton to access the DB
    companion object{
        @Volatile
        private var INSTANCE: NoteDB?= null
        private val LOCK = Any()
        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK){
            INSTANCE ?: createDatabase(context).also{
                INSTANCE = it
            }
        }
        private fun createDatabase(context: Context)= Room.databaseBuilder(
                    context.applicationContext,
                    NoteDB::class.java,
                    "app_database").build()
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}