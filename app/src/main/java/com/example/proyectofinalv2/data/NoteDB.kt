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
    entities = [Note::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class NoteDB : RoomDatabase() {
    abstract fun noteDao():NoteDao

    // Singleton to access the DB
    companion object{
        @Volatile
        private var INSTANCE: NoteDB?= null
        fun getAppDatabase(context: Context): NoteDB?{
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder<NoteDB>(
                    context.applicationContext, NoteDB::class.java, "NoteDB")
                    .allowMainThreadQueries().build()
            }
            return INSTANCE
        }
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}