package com.gamdestroyerr.roomnote.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_IMAGE_CAPTURE = 100
private lateinit var photoFile: File
private const val THUMBSIZE = 300

class NoteContentFragment : Fragment(R.layout.fragment_note_content) {

    private lateinit var navController: NavController
    private lateinit var noteActivityViewModel: NoteActivityViewModel
    private lateinit var result: String
    private var note: Note? = null
    private var imageThumbnail: Bitmap? = null
    private val currentDate = SimpleDateFormat.getDateInstance().format(Date())
    private var color = -1
    private var currentPhotoPath: String? = null

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
                R.style.BottomSheetDialogTheme,
            )
            val bottomSheetView: View = layoutInflater.inflate(
                R.layout.bottom_sheet_dialog,
                null,
            )

            with(bottomSheetDialog) {
                setContentView(bottomSheetView)
                show()
            }

            bottomSheetView.colorPicker.setSelectedColor(color)
            bottomSheetView.colorPicker.setOnColorSelectedListener { value ->
                color = value
                noteContentFragmentParent.apply {
                    setBackgroundColor(color)
                    activity.window.statusBarColor = color
                    toolbarFragmentNoteContent.setBackgroundColor(color)
                    appBarLayout2.setBackgroundColor(color)
                }
            }
            bottomSheetView.addImage.setOnClickListener {
                val permission = ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                )
                if (permission != PackageManager.PERMISSION_GRANTED) {

                    val permissionArray = arrayOf(Manifest.permission.CAMERA)
                    ActivityCompat.requestPermissions(
                        activity,
                        permissionArray,
                        REQUEST_IMAGE_CAPTURE
                    )
                }
                if (permission == PackageManager.PERMISSION_GRANTED) {
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
            setImage(note?.imagePath)

            if (note == null) {
                lastEdited.text =
                    getString(R.string.edited_on, SimpleDateFormat.getDateInstance().format(Date()))
            } else {
                lastEdited.text = getString(R.string.edited_on, note?.date)
                color = note!!.color
                if (noteActivityViewModel.setImagePath() != null) {
                    setImage(noteActivityViewModel.setImagePath())
                } else {
                    noteActivityViewModel.saveImagePath(note?.imagePath)
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

            when {
                note == null -> {
                    saveNote()
                    result = "Note Saved"
                    setFragmentResult("key", bundleOf("bundleKey" to result))
                    navController.navigate(R.id.action_noteContentFragment_to_noteFragment)

                }
                note != null -> {
                    updateNote()
                    result = "Content Changed"
                    setFragmentResult("key", bundleOf("bundleKey" to result))
                    navController.navigate(R.id.action_noteContentFragment_to_noteFragment)
                }
            }
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
                noteActivityViewModel.setImagePath(),
                imageThumbnail
            )
        )
    }

    private fun updateNote() {
        imageThumbnail = ThumbnailUtils.extractThumbnail(
            BitmapFactory.decodeFile(noteActivityViewModel.setImagePath()),
            THUMBSIZE,
            THUMBSIZE,
        )
        noteActivityViewModel.updateNote(

            Note(
                note!!.id,
                titleTxtView.text.toString(),
                noteContentTxtView.text.toString(),
                currentDate,
                color,
                noteActivityViewModel.setImagePath(),
                imageThumbnail
            )
        )
    }

    @Suppress("DEPRECATION")
    private fun takePictureIntent() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { captureIntent ->
            photoFile = getPhotoFile()
            val fileProvider = FileProvider.getUriForFile(
                requireContext(),
                getString(R.string.fileAuthority),
                photoFile
            )
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            captureIntent.resolveActivity(activity?.packageManager!!.also {
                startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE)
            })
        }
    }

    private fun getPhotoFile(): File {
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timeStamp: String =
            SimpleDateFormat("yyyy_MM_dd_HH:mm:ss", Locale.getDefault()).format(Date())
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
        if (Integer.parseInt(file.length().toString()) == 0) {
            file.delete()
        }
        return file
    }

    private fun setImage(filePath: String?) {
        if (filePath != null) {
            val uri = Uri.fromFile(File(filePath))
            noteImage.load(uri) {
                this.placeholder(R.drawable.image_placeholder)
            }
            noteImage.visibility = View.VISIBLE
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            noteActivityViewModel.saveImagePath(photoFile.absolutePath)
            setImage(photoFile.absolutePath)
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

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                val toDelete = File(note?.imagePath)
                if (toDelete.exists()) {
                    toDelete.delete()
                }
                note?.thumbnail = null
                noteActivityViewModel.saveImagePath(null)
                noteImage.visibility = View.GONE
                Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onContextItemSelected(item)
    }
}