package com.gamdestroyerr.roomnote.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gamdestroyerr.roomnote.repository.NoteRepository

class NoteActivityViewModelFactory(val app: Application, private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteActivityViewModel(app, repository) as T
    }
}