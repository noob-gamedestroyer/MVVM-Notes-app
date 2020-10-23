package com.gamdestroyerr.roomnote.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gamdestroyerr.roomnote.R
import com.gamdestroyerr.roomnote.model.Note
import com.gamdestroyerr.roomnote.ui.fragments.NoteFragmentDirections
import com.gamdestroyerr.roomnote.utils.hideKeyboard
import com.gamdestroyerr.roomnote.utils.loadImage
import com.google.android.material.textview.MaterialTextView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import kotlinx.android.synthetic.main.note_item_layout.view.*
import org.commonmark.node.SoftLineBreak
import java.io.File


class RvNotesAdapter : androidx.recyclerview.widget.ListAdapter<
        Note,
        RvNotesAdapter.NotesViewHolder>(
    DiffUtilCallback()
) {

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: MaterialTextView = itemView.noteItemTitle
        val content: TextView = itemView.noteContentItemTitle
        val date: MaterialTextView = itemView.noteDate
        val image: ImageView = itemView.itemNoteImage
        val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.note_item_rounded)
        val noteContainer: ConstraintLayout = itemView.noteItemContainer
        val markWon = Markwon.builder(itemView.context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(itemView.context))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(
                        SoftLineBreak::class.java
                    ) { visitor, _ -> visitor.forceNewLine() }
                }
            })
            .build()
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
                markWon.setMarkdown(content, note.content)
                date.text = note.date
                if (note.imagePath != null) {
                    image.visibility = View.VISIBLE
                    val uri = Uri.fromFile(File(note.imagePath))
                    if (File(note.imagePath).exists())
                        itemView.context.loadImage(uri, image)
                } else {
                    Glide.with(itemView).clear(image)
                    image.visibility = View.GONE
                }
                drawable?.setTint(note.color)
                noteContainer.background = ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.note_item_rounded
                )
                itemView.setOnClickListener {
                    val action = NoteFragmentDirections.actionNoteFragmentToNoteContentFragment()
                        .setNote(note)
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action)
                }
                itemView.noteContentItemTitle.setOnClickListener {
                    val action = NoteFragmentDirections.actionNoteFragmentToNoteContentFragment()
                        .setNote(note)
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action)
                }
            }
        }
    }
}