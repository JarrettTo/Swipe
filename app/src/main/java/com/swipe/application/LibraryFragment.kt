package com.swipe.application

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LibraryFragment : Fragment() {
    private lateinit var groupView: RecyclerView
    private lateinit var adapter: LibraryAdapter
    private val playlistList: ArrayList<Playlist> = DataHelper.initializePlaylist()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.library, container, false)

        groupView = view.findViewById(R.id.LibraryRecycler)
        groupView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LibraryAdapter(playlistList)
        adapter.isNotDeleteMode = true
        groupView.adapter = adapter

        val addButton: Button = view.findViewById(R.id.createButton)
        val delButton: Button = view.findViewById(R.id.deleteButton)

        addButton.setOnClickListener {
            showAddPlaylistDialog()
        }

        delButton.setOnClickListener {
            val isCancelMode = delButton.text.toString() == "  Cancel  "
            adapter.isNotDeleteMode = isCancelMode
            adapter.notifyDataSetChanged()

            delButton.isSelected = !isCancelMode
            if (isCancelMode) {
                delButton.text = "  Remove Playlist  "
            } else {
                delButton.text = "  Cancel  "
                // Implement the delete functionality here
            }
        }

        return view
    }

    private fun showAddPlaylistDialog() {
        val dialogView = layoutInflater.inflate(R.layout.create_playlist, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)

        val alertDialog = dialogBuilder.create()

        val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)
        val createButton: Button = dialogView.findViewById(R.id.createButton)
        val playlistNameEditText: EditText = dialogView.findViewById(R.id.playlistName)

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        createButton.setOnClickListener {
            val playlistName = playlistNameEditText.text.toString().trim()
            // Handle the creation of the playlist
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}
