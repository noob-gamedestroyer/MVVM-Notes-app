package com.gamdestroyerr.roomnote.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.gamdestroyerr.roomnote.R
import com.gamdestroyerr.roomnote.db.Note
import com.gamdestroyerr.roomnote.ui.activity.NoteActivity
import com.gamdestroyerr.roomnote.viewmodel.NoteActivityViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_note_content.*
import kotlinx.android.synthetic.main.fragment_note_content.view.*
import java.text.SimpleDateFormat
import java.util.*

class NoteContentFragment : Fragment(R.layout.fragment_note_content) {

    private lateinit var navController: NavController
    private lateinit var noteActivityViewModel: NoteActivityViewModel
    private var note: Note? = null
    private lateinit var result: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        noteActivityViewModel = (activity as NoteActivity).noteActivityViewModel

        val count = parentFragmentManager.backStackEntryCount
        Log.d("backStackCount", count.toString())

        scrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY >= oldScrollY) {
                deleteFab.hide()
            } else if(scrollY < oldScrollY) {
                deleteFab.show()
            }
        }

        view.saveBtn.setOnClickListener {
            hideKeyboard()
            saveNoteViaFragmentAndGoBack()
        }
        view.backBtn.setOnClickListener {
            hideKeyboard()
            saveNoteViaFragmentAndGoBack()
        }

        deleteFab.setOnClickListener {
            if (note != null) {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    background = getDrawable(requireContext(), R.drawable.note_item_rounded)
                    setTitle("Delete Note?")
                    setMessage("You cannot Undo this!!")
                    setIcon(getDrawable(requireContext(), R.drawable.ic_round_delete_24))
                    setPositiveButton("Delete") { _: DialogInterface, _: Int ->

                        noteActivityViewModel.deleteNote(
                            Note(
                                note!!.id,
                                note!!.title,
                                note!!.content,
                                note!!.date
                            )
                        )
                        result = "Note Deleted"
                        setFragmentResult("key", bundleOf("bundleKey" to result))
                        navController.navigate(R.id.action_noteContentFragment_to_noteFragment)

                    }
                    setNegativeButton("cancel") { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }
                }.show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.delete_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        arguments?.let {
            note = NoteContentFragmentArgs.fromBundle(it).note
            titleTxtView.setText(note?.title)
            noteContentTxtView.setText(note?.content)
            if (note == null){
                lastEdited.text =
                    getString(R.string.edited_on, SimpleDateFormat.getDateInstance().format(Date()))
            } else {
                lastEdited.text = getString(R.string.edited_on, note?.date)
            }
        }
    }

    private fun saveNoteViaFragmentAndGoBack() {
        val currentDate = SimpleDateFormat.getDateInstance().format(Date())

        if (titleTxtView.text.toString().isEmpty() &&
            noteContentTxtView.text.toString().isEmpty()
        ) {
            result = "Empty Note Discarded"
            setFragmentResult("key", bundleOf("bundleKey" to result))
            navController.navigate(R.id.action_noteContentFragment_to_noteFragment)

        } else {

            if (note == null) {
                noteActivityViewModel.saveNote(
                    Note(
                        0,
                        titleTxtView.text.toString(),
                        noteContentTxtView.text.toString(),
                        currentDate
                    )
                )
                result = "Note Saved"
                setFragmentResult("key", bundleOf("bundleKey" to result))
                navController.navigate(R.id.action_noteContentFragment_to_noteFragment)

            } else {
                noteActivityViewModel.updateNote(
                    Note(
                        note!!.id,
                        titleTxtView.text.toString(),
                        noteContentTxtView.text.toString(),
                        currentDate
                    )
                )

                navController.navigate(R.id.action_noteContentFragment_to_noteFragment)
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            (activity as NoteActivity).getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            requireView().applicationWindowToken,
            0
        )
    }

}