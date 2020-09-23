package com.gamdestroyerr.roomnote.ui.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gamdestroyerr.roomnote.R
import com.gamdestroyerr.roomnote.adapters.RvNotesAdapter
import com.gamdestroyerr.roomnote.ui.activity.NoteActivity
import com.gamdestroyerr.roomnote.viewmodel.NoteActivityViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_note.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NoteFragment : Fragment(R.layout.fragment_note) {

    private lateinit var noteActivityViewModel: NoteActivityViewModel


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

        recyclerViewDisplay()

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
        rv_note.setHasFixedSize(true)
        rv_note.setItemViewCacheSize(2)
        noteActivityViewModel.getAllNotes().observe(viewLifecycleOwner, { list ->
            rv_note.adapter = RvNotesAdapter(list)
        })

    }


}