package com.gamdestroyerr.roomnote.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.gamdestroyerr.roomnote.R
import com.gamdestroyerr.roomnote.model.Note
import com.gamdestroyerr.roomnote.ui.activity.NoteActivity
import com.gamdestroyerr.roomnote.viewmodel.NoteActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.android.synthetic.main.fragment_note_content.*
import kotlinx.android.synthetic.main.fragment_note_content.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NoteContentFragment : Fragment(R.layout.fragment_note_content) {

    private lateinit var navController: NavController
    private lateinit var noteActivityViewModel: NoteActivityViewModel
    private var note: Note? = null
    private lateinit var result: String
    private var color = -1

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout2.visibility = View.INVISIBLE
        noteContentTxtView.visibility = View.INVISIBLE
        titleTxtView.visibility = View.INVISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            appBarLayout2.visibility = View.VISIBLE
            noteContentTxtView.visibility = View.VISIBLE
            titleTxtView.visibility = View.VISIBLE
        }

        navController = Navigation.findNavController(view)
        val activity = activity as NoteActivity
        noteActivityViewModel = activity.noteActivityViewModel

        val count = parentFragmentManager.backStackEntryCount
        Log.d("backStackCount", count.toString())

        view.saveBtn.setOnClickListener {
            hideKeyboard()
            saveNoteViaFragmentAndGoBack()
        }
        view.backBtn.setOnClickListener {
            hideKeyboard()
            saveNoteViaFragmentAndGoBack()
        }

        view.deleteFab.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                requireContext(),
                R.style.BottomSheetDialogTheme
            )
            val bottomSheetView: View = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

            bottomSheetView.colorPicker.setSelectedColor(color)
            bottomSheetView.colorPicker.setOnColorSelectedListener {
                color = it
                noteContentFragmentParent.apply {
                    setBackgroundColor(color)
                    activity.window.statusBarColor = color
                    toolbarFragmentNoteContent.setBackgroundColor(color)
                    appBarLayout2.setBackgroundColor(color)
                }
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
            bottomSheetDialog.setOnDismissListener {
                Toast.makeText(requireContext(), "Color Set", Toast.LENGTH_SHORT).show()
            }
        }

        //opens with existing note item
        arguments?.let {
            note = NoteContentFragmentArgs.fromBundle(it).note
            titleTxtView.setText(note?.title)
            noteContentTxtView.setText(note?.content)

            if (note == null) {
                lastEdited.text =
                    getString(R.string.edited_on, SimpleDateFormat.getDateInstance().format(Date()))
            } else {
                lastEdited.text = getString(R.string.edited_on, note?.date)
                color = note!!.color
                noteContentFragmentParent.apply {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(10)
                        setBackgroundColor(color)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(295)
                        activity.window.statusBarColor = color
                    }
                }
                toolbarFragmentNoteContent.setBackgroundColor(color)
                appBarLayout2.setBackgroundColor(color)

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
                        currentDate,
                        color
                    )
                )
                Log.d("tag", "new note saved")
                result = "Note Saved"
                setFragmentResult("key", bundleOf("bundleKey" to result))
                navController.navigate(R.id.action_noteContentFragment_to_noteFragment)

            } else if (note != null) {
                noteActivityViewModel.updateNote(
                    Note(
                        note!!.id,
                        titleTxtView.text.toString(),
                        noteContentTxtView.text.toString(),
                        currentDate,
                        color
                    )
                )
                Log.d("tag", "new Note Saved")
                navController.navigate(R.id.action_noteContentFragment_to_noteFragment)
            }
            Log.d("tag", "skipped")
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