package com.gamdestroyerr.roomnote.ui.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gamdestroyerr.roomnote.R
import com.gamdestroyerr.roomnote.adapters.RvNotesAdapter
import com.gamdestroyerr.roomnote.ui.activity.NoteActivity
import com.gamdestroyerr.roomnote.utils.SwipeToDelete
import com.gamdestroyerr.roomnote.utils.hideKeyboard
import com.gamdestroyerr.roomnote.viewmodel.NoteActivityViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_note.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class NoteFragment : Fragment(R.layout.fragment_note) {

    private lateinit var noteActivityViewModel: NoteActivityViewModel
    private lateinit var adapter: RvNotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as NoteActivity
        activity.window.statusBarColor = Color.WHITE
        noteActivityViewModel = activity.noteActivityViewModel
        val navController = Navigation.findNavController(view)

        requireView().hideKeyboard()

        appBarLayout1.visibility = View.GONE
        CoroutineScope(Dispatchers.Main).launch {
            delay(1)
            appBarLayout1.visibility = View.VISIBLE
        }


        val count = parentFragmentManager.backStackEntryCount
        Log.d("backStackCount", count.toString())
        noteActivityViewModel.saveImagePath(null)  //temporary fix


        //Receives confirmation from the noteContentFragment
        setFragmentResultListener("key") { _, bundle ->
            when (val result = bundle.getString("bundleKey")) {
                "Note Saved", "Empty Note Discarded" -> {

                    CoroutineScope(Dispatchers.Main).launch {
                        Snackbar.make(view, result, Snackbar.LENGTH_SHORT).apply {
                            animationMode = Snackbar.ANIMATION_MODE_FADE
                            setAnchorView(R.id.saveFab)
                        }.show()
                        rv_note.visibility = View.GONE
                        delay(300)
                        recyclerViewDisplay()
                        rv_note.visibility = View.VISIBLE
                    }
                }
                "Content Changed" -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        Snackbar.make(view, "Refreshing...", Snackbar.LENGTH_SHORT).apply {
                            animationMode = Snackbar.ANIMATION_MODE_FADE
                            setAnchorView(R.id.saveFab)
                            duration = 300
                        }.show()
                        rv_note.visibility = View.GONE
                        delay(300)
                        recyclerViewDisplay()
                        rv_note.visibility = View.VISIBLE
                    }
                }
            }
        }

        //sets up RecyclerView
        recyclerViewDisplay()
        swipeToDelete(rv_note)

        //implements search function
        search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                no_data.visibility = View.GONE
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString().isNotEmpty()) {
                    clear_text.visibility = View.VISIBLE
                    val text = s.toString()
                    val query = "%$text%"
                    if (query.isNotEmpty()) {
                        noteActivityViewModel.searchNote(query).observe(viewLifecycleOwner, {
                            adapter.submitList(it)
                        })
                    } else {
                        observerDataChanges()
                    }
                } else {
                    observerDataChanges()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    clear_text.visibility = View.GONE
                }
            }

        })

        search.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                v.clearFocus()
                requireView().hideKeyboard()
            }
            return@setOnEditorActionListener true
        }

        clear_text.setOnClickListener {
            clearTxtFunction()
            it.visibility = View.GONE
            no_data.visibility = View.GONE
        }



        view.saveFab.setOnClickListener {
            appBarLayout1.visibility = View.INVISIBLE
            navController.navigate(R.id.action_noteFragment_to_noteContentFragment)
        }
        view.innerFab.setOnClickListener {
            navController.navigate(R.id.action_noteFragment_to_noteContentFragment)
        }



        rv_note.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->
            when {
                scrollY > oldScrollY -> {
                    chat_fab_text?.visibility = View.GONE

                }
                scrollX == scrollY -> {
                    chat_fab_text?.visibility = View.VISIBLE

                }
                else -> {
                    chat_fab_text?.visibility = View.VISIBLE

                }
            }
        }
    } //onViewCreated closed

    private fun recyclerViewDisplay() {
        @SuppressLint("SwitchIntDef")
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                setUpRecyclerView(2)
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                setUpRecyclerView(3)
            }
        }
    }

    private fun setUpRecyclerView(spanCount: Int) {
        rv_note.layoutManager =
            StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        rv_note.setHasFixedSize(true)
        adapter = RvNotesAdapter()
        rv_note.adapter = adapter
        rv_note.itemAnimator = SlideInDownAnimator().apply {
            addDuration = 250
        }
        observerDataChanges()
    }

    private fun observerDataChanges() {
        noteActivityViewModel.getAllNotes().observe(viewLifecycleOwner, { list ->
            if (list.isEmpty()) {
                no_data.visibility = View.VISIBLE
            } else {
                no_data.visibility = View.GONE
            }
            adapter.submitList(list)
        })
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {

        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = adapter.currentList[position]
                var actionBtnTapped = false
                noteActivityViewModel.deleteNote(note)
                search.apply {
                    hideKeyboard()
                    clearFocus()
                }
                if (search.text.toString().isEmpty()) {
                    observerDataChanges()
                }
                val snackBar = Snackbar.make(
                    requireView(), "Note Deleted", Snackbar.LENGTH_LONG
                ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        when (!actionBtnTapped) {
                            (note?.imagePath?.isNotEmpty()) -> {
                                val toDelete = File(note.imagePath)
                                if (toDelete.exists()) {
                                    toDelete.delete()
                                }
                            }
                        }
                        super.onDismissed(transientBottomBar, event)
                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        transientBottomBar?.setAction("UNDO") {
                            noteActivityViewModel.saveNote(note)
                            no_data.visibility = View.GONE
                            actionBtnTapped = true

                        }
                        super.onShown(transientBottomBar)
                    }
                }).apply {
                    animationMode = Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.saveFab)
                }
                snackBar.setActionTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.yellow
                    )
                )
                snackBar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun clearTxtFunction() {
        search.apply {
            text.clear()
            hideKeyboard()
            clearFocus()
            observerDataChanges()
        }
    }

} //class NoteFragment closed


