package com.gamdestroyerr.roomnote.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
import com.gamdestroyerr.roomnote.viewmodel.NoteActivityViewModel
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_note.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NoteFragment : Fragment(R.layout.fragment_note) {

    private lateinit var noteActivityViewModel: NoteActivityViewModel
    private lateinit var adapter: RvNotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout1.visibility = View.GONE
        CoroutineScope(Dispatchers.Main).launch {
            delay(1)
            appBarLayout1.visibility = View.VISIBLE
        }
        val activity = activity as NoteActivity
        activity.window.statusBarColor = Color.WHITE
        noteActivityViewModel = activity.noteActivityViewModel
        val navController = Navigation.findNavController(view)

        val count = parentFragmentManager.backStackEntryCount
        Log.d("backStackCount", count.toString())

        //Receives confirmation from the noteContentFragment
        setFragmentResultListener("key") { _, bundle ->
            val result = bundle.getString("bundleKey")
            if (result!!.isNotEmpty()) {

                CoroutineScope(Dispatchers.Unconfined).launch {
                    delay(10)
                    Snackbar.make(view, result, Snackbar.LENGTH_LONG).apply {
                        animationMode = Snackbar.ANIMATION_MODE_FADE
                        setAnchorView(R.id.saveFab)
                    }.show()
                }
            }
        }
        //sets up RecyclerView
        recyclerViewDisplay()
        swipeToDelete(rv_note)

        //implements search function
        search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clear_text.visibility = View.VISIBLE
                val text = s.toString()
                val query = "%$text%"
                noteActivityViewModel.searchNote(query).observe(viewLifecycleOwner, {
                    adapter.submitList(it)
                })
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
                hideKeyboard()
            }
            return@setOnEditorActionListener true
        }
        clear_text.setOnClickListener {
            search.apply {
                text.clear()
                hideKeyboard()
                clearFocus()
            }
            it.visibility = View.GONE
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
                    chat_fab_text!!.visibility = View.GONE

                }
                scrollX == scrollY -> {
                    chat_fab_text!!.visibility = View.VISIBLE

                }
                else -> {
                    chat_fab_text!!.visibility = View.VISIBLE

                }
            }
        }

    } //onViewCreated closed

    private fun recyclerViewDisplay() {

        fun setUpRecyclerView(spanCount: Int) {
            rv_note.layoutManager =
                StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)

            adapter = RvNotesAdapter()
            rv_note.adapter = adapter
            rv_note.itemAnimator = SlideInDownAnimator().apply {
                addDuration = 250
            }
            noteActivityViewModel.getAllNotes().observe(viewLifecycleOwner, { list ->
                adapter.submitList(list)
            })
        }

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

    private fun swipeToDelete(recyclerView: RecyclerView) {

        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = adapter.currentList[position]
                noteActivityViewModel.deleteNote(note)
                val snackBar = Snackbar.make(
                    requireView(), "Note Deleted", Snackbar.LENGTH_LONG
                ).apply {
                    setAction("UNDO") {
                        noteActivityViewModel.saveNote(note)
                    }
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

    private fun hideKeyboard() {
        val inputMethodManager =
            (activity as NoteActivity).getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            requireView().applicationWindowToken,
            0
        )
    }

} //class NoteFragment closed


