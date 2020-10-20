package com.gamdestroyerr.roomnote.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.gamdestroyerr.roomnote.R
import com.gamdestroyerr.roomnote.db.NoteDatabase
import com.gamdestroyerr.roomnote.repository.NoteRepository
import com.gamdestroyerr.roomnote.viewmodel.NoteActivityViewModel
import com.gamdestroyerr.roomnote.viewmodel.NoteActivityViewModelFactory

class NoteActivity : AppCompatActivity() {

    lateinit var noteActivityViewModel: NoteActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        val noteRepository = NoteRepository(NoteDatabase(this))
        val noteViewModelProviderFactory = NoteActivityViewModelFactory(noteRepository)
        noteActivityViewModel = ViewModelProvider(
            this,
            noteViewModelProviderFactory
        )[NoteActivityViewModel::class.java]
    }

}