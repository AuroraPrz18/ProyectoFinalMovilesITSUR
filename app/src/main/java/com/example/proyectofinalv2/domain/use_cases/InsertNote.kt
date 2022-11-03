package com.example.proyectofinalv2.domain.use_cases

import com.example.proyectofinalv2.domain.model.Note
import com.example.proyectofinalv2.domain.repository.NoteRepository
import javax.inject.Inject

class InsertNote @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.insertNote(note)
    }
}