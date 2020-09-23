package com.gamdestroyerr.roomnote.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DAO {

    @Insert
    suspend fun addNote(note: Note)

    @Update (onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: Note)

    @Query("SELECT * FROM Note ORDER BY id DESC")
    fun getAllNote() : LiveData<MutableList<Note>>

    @Delete
    suspend fun deleteNote(note: Note)

}