package com.gamdestroyerr.roomnote.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import coil.load
import com.gamdestroyerr.roomnote.R
import com.gamdestroyerr.roomnote.model.Note
import com.gamdestroyerr.roomnote.ui.activity.NoteActivity
import com.gamdestroyerr.roomnote.utils.hideKeyboard
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
    private val REQUEST_IMAGE_CAPTURE = 100
    private var imageBitmap: Bitmap? = null
    private val currentDate = SimpleDateFormat.getDateInstance().format(Date())

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

        if (count == 1 && note == null) {
            noteImage.visibility = View.VISIBLE
            noteImage.load(noteActivityViewModel.setImage())
        }

        registerForContextMenu(noteImage)

        view.saveBtn.setOnClickListener {
            requireView().hideKeyboard()
            saveNoteViaFragmentAndGoBack()
        }
        view.backBtn.setOnClickListener {
            requireView().hideKeyboard()
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
            with(bottomSheetDialog) {
                setContentView(bottomSheetView)
                show()
            }

            bottomSheetView.addImage.setOnClickListener {
                val permission = ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                )
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    val permissions = arrayOf(Manifest.permission.CAMERA)
                    ActivityCompat.requestPermissions(activity, permissions, REQUEST_IMAGE_CAPTURE)
                } else if (permission == PackageManager.PERMISSION_GRANTED) {
                    takePictureIntent()
                    bottomSheetDialog.dismiss()
                }

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
                if (note?.image != null) {
                    noteImage.load(note?.image)
                    noteActivityViewModel.saveImage(note?.image)
                }
                noteContentFragmentParent.apply {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(10)
                        setBackgroundColor(color)
                        noteImage.visibility = View.VISIBLE
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

        if (titleTxtView.text.toString().isEmpty() &&
            noteContentTxtView.text.toString().isEmpty()
        ) {
            result = "Empty Note Discarded"
            setFragmentResult("key", bundleOf("bundleKey" to result))
            navController.navigate(R.id.action_noteContentFragment_to_noteFragment)

        } else {

            if (note == null) {
                saveNote()
                result = "Note Saved"
                setFragmentResult("key", bundleOf("bundleKey" to result))
                navController.navigate(R.id.action_noteContentFragment_to_noteFragment)

            } else if (note != null) {
                updateNote()
                if (noteActivityViewModel.setImage() != note?.image || color != note?.color) {
                    result = "Content Changed"
                    setFragmentResult("key", bundleOf("bundleKey" to result))
                }
                navController.navigate(R.id.action_noteContentFragment_to_noteFragment)
            }
            Log.d("tag", "skipped")
        }
    }

    private fun saveNote() {
        noteActivityViewModel.saveNote(
            Note(
                0,
                titleTxtView.text.toString(),
                noteContentTxtView.text.toString(),
                currentDate,
                color,
                imageBitmap
            )
        )
    }

    private fun updateNote() {
        noteActivityViewModel.updateNote(
            Note(
                note!!.id,
                titleTxtView.text.toString(),
                noteContentTxtView.text.toString(),
                currentDate,
                color,
                noteActivityViewModel.setImage()
            )
        )
    }

    @Suppress("DEPRECATION")
    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(activity?.packageManager!!.also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            })
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap
            Log.d("tag", imageBitmap.toString())
            noteActivityViewModel.saveImage(imageBitmap!!)
            noteImage.load(imageBitmap)
            noteImage.visibility = View.VISIBLE
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(
            0,
            1,
            1,
            menuIconWithText(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_round_delete_24
                )!!, getString(R.string.delete)
            )
        )
    }

    private fun menuIconWithText(r: Drawable, title: String): CharSequence? {
        r.setBounds(0, 0, r.intrinsicWidth, r.intrinsicHeight)
        val sb = SpannableString("   $title")
        val imageSpan = ImageSpan(r, ImageSpan.ALIGN_BOTTOM)
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                note?.image = null
                noteActivityViewModel.saveImage(null)
                noteImage.visibility = View.GONE
            }
        }
        return super.onContextItemSelected(item)
    }
}