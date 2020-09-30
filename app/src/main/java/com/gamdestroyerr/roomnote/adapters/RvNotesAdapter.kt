package com.gamdestroyerr.roomnote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.gamdestroyerr.roomnote.R
import com.gamdestroyerr.roomnote.model.Note
import com.gamdestroyerr.roomnote.ui.fragments.NoteFragmentDirections
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.note_item_layout.view.*


class RvNotesAdapter : androidx.recyclerview.widget.ListAdapter<
        Note,
        RvNotesAdapter.NotesViewHolder>(
    DiffUtilCallback()
){

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: MaterialTextView = itemView.noteItemTitle
        val content: MaterialTextView = itemView.noteContentItemTitle
        val date: MaterialTextView = itemView.noteDate
        val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.note_item_rounded)
        val noteContainer: ConstraintLayout = itemView.noteItemContainer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.note_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {

        getItem(position).let { note ->

            holder.apply {
                title.text = note.title
                content.text = note.content
                date.text = note.date
                drawable!!.setTint(note.color)
                noteContainer.background = ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.note_item_rounded
                )
                itemView.setOnClickListener {
                    val action = NoteFragmentDirections.actionNoteFragmentToNoteContentFragment()
                        .setNote(note)
                    Navigation.findNavController(it).navigate(action)
                }
            }
        }
    }

}

