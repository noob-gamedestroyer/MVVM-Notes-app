package com.gamdestroyerr.roomnote.ui.activity

import android.os.Bundle
import android.widget.Toast
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
        try {
            setContentView(R.layout.activity_note)
            val noteRepository = NoteRepository(NoteDatabase(this))
            val noteViewModelProviderFactory = NoteActivityViewModelFactory(noteRepository)
            noteActivityViewModel = ViewModelProvider(
                this,
                noteViewModelProviderFactory
            )[NoteActivityViewModel::class.java]
        } catch (e: Exception) {
            Toast.makeText(this, "error occurred", Toast.LENGTH_SHORT).show()
        }
    }

}