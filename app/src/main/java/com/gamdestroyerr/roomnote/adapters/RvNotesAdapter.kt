package com.gamdestroyerr.roomnote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.gamdestroyerr.roomnote.R
import com.gamdestroyerr.roomnote.db.Note
import com.gamdestroyerr.roomnote.ui.fragments.NoteFragmentDirections
import kotlinx.android.synthetic.main.note_item_layout.view.*

class RvNotesAdapter(var note: List<Note>) :
    RecyclerView.Adapter<RvNotesAdapter.NotesViewHolder>() {

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

//    private val diffCallback = object :DiffUtil.ItemCallback<Note>() {
//        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
//            return oldItem == newItem
//        }
//
//    }
//
//    private val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.note_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.itemView.apply {
            noteItemContainer.animation =
                AnimationUtils.loadAnimation(this.context, R.anim.fade_scale)
            this.noteItemTitle.text = note[position].title
            this.noteContentItemTitle.text = note[position].content
            this.noteDate.text = note[position].date
        }
        holder.itemView.setOnClickListener {
            val action = NoteFragmentDirections.actionNoteFragmentToNoteContentFragment()
                .setNote(note[position])
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return note.size
    }

}

