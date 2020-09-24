package com.gamdestroyerr.roomnote.ui.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
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

        noteActivityViewModel = (activity as NoteActivity).noteActivityViewModel
        val navController = Navigation.findNavController(view)

        val count = parentFragmentManager.backStackEntryCount
        Log.d("backStackCount", count.toString())

        setFragmentResultListener("key") { _, bundle ->
            val result = bundle.getString("bundleKey")
            if (result!!.isNotEmpty()) {

                CoroutineScope(Dispatchers.Main).launch {
                    delay(700)
                    Snackbar.make(view, result, Snackbar.LENGTH_LONG).apply {
                        duration = 2500
                        animationMode = Snackbar.ANIMATION_MODE_FADE
                    }.show()
                }
            }
        }

        //sets up RecyclerView
        recyclerViewDisplay()
        swipeToDelete(rv_note)

        view.saveFab.setOnClickListener {
            navController.navigate(R.id.action_noteFragment_to_noteContentFragment)
        }

    }

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

        adapter = RvNotesAdapter()
        rv_note.adapter = adapter
        rv_note.itemAnimator = SlideInDownAnimator().apply {
            addDuration = 300
        }
        noteActivityViewModel.getAllNotes().observe(viewLifecycleOwner, Observer{ list ->
            adapter.submitList(list)
        })
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {

        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = adapter.currentList[position]
                noteActivityViewModel.deleteNote(note)
                val snackBar = Snackbar.make(
                    requireView(),"Note Deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("UNDO") {
                        noteActivityViewModel.saveNote(note)
                    }
                }
                snackBar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
                snackBar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}


