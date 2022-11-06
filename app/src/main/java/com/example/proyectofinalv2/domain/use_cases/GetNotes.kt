package com.example.proyectofinalv2.domain.use_cases

import com.example.proyectofinalv2.domain.model.Note
import com.example.proyectofinalv2.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotes @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): List<Note> {
        return repository.getNotes()
    }
}