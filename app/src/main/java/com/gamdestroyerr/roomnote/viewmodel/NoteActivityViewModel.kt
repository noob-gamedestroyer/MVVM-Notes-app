package com.gamdestroyerr.roomnote.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamdestroyerr.roomnote.db.Note
import com.gamdestroyerr.roomnote.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteActivityViewModel(private val repositoryObject: NoteRepository) : ViewModel() {


    fun saveNote(newNote: Note) = viewModelScope.launch {
        Log.d("tag", "saveNote() is called")
        repositoryObject.addNote(newNote)
    }

    fun updateNote(existingNote: Note) = viewModelScope.launch {
        repositoryObject.updateNote(existingNote)
    }

    fun deleteNote(existingNote: Note) = viewModelScope.launch {
        repositoryObject.deleteNote(existingNote)
    }

    fun getAllNotes(): LiveData<MutableList<Note>> = repositoryObject.getNote()
}