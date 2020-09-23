package com.gamdestroyerr.roomnote.repository
import android.util.Log
import com.gamdestroyerr.roomnote.db.Note
import com.gamdestroyerr.roomnote.db.NoteDatabase

class NoteRepository (
   private val db: NoteDatabase,
) {

   suspend fun addNote(note: Note) {
      db.getNoteDao().addNote(note)
      Log.d("tag", "add note is called")
   }

   suspend fun updateNote(note: Note) = db.getNoteDao().updateNote(note)

   fun getNote() = db.getNoteDao().getAllNote()

   suspend fun  deleteNote(note: Note) = db.getNoteDao().deleteNote(note)

}