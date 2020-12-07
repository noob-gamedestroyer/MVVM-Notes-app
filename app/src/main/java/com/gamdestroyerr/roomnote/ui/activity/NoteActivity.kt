package com.gamdestroyerr.roomnote.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.gamdestroyerr.roomnote.databinding.ActivityNoteBinding
import com.gamdestroyerr.roomnote.db.NoteDatabase
import com.gamdestroyerr.roomnote.repository.NoteRepository
import com.gamdestroyerr.roomnote.utils.shortToast
import com.gamdestroyerr.roomnote.viewmodel.NoteActivityViewModel
import com.gamdestroyerr.roomnote.viewmodel.NoteActivityViewModelFactory

class NoteActivity : AppCompatActivity() {

    lateinit var noteActivityViewModel: NoteActivityViewModel
    private lateinit var binding: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        try {
            setContentView(binding.root)
            val noteRepository = NoteRepository(NoteDatabase(this))
            val noteViewModelProviderFactory = NoteActivityViewModelFactory(noteRepository)
            noteActivityViewModel = ViewModelProvider(
                this,
                noteViewModelProviderFactory
            )[NoteActivityViewModel::class.java]
        } catch (e: Exception) {
            shortToast("error occurred")
        }
    }

}