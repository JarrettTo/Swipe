package com.swipe.application

import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

interface PlaylistActionListener {
    fun onDeletePlaylistAction(name: String)
}

class LibraryFragment : Fragment() , PlaylistActionListener{
    override fun onDeletePlaylistAction(name: String) {
        deletePlaylist(name)
    }

    private lateinit var userSession: UserSession
    private lateinit var groupView: RecyclerView
    private lateinit var adapter: LibraryAdapter
    private lateinit var playlistList: ArrayList<Playlist>
    private val playlistDataHelper = PlaylistDataHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.library, container, false)

        groupView = view.findViewById(R.id.LibraryRecycler)
        groupView.layoutManager = LinearLayoutManager(requireContext())

        userSession = UserSession(requireContext())
        val playlists = userSession.playlist

        Log.d("PLAYLIST-IDS:", "CHECK ${playlists}")
        lifecycleScope.launch {
            playlistList = playlistDataHelper.retrievePlaylists(playlists)
            Log.d("PLAYLIST:", "CHECK ${playlistList}")
            adapter = LibraryAdapter(playlistList, this@LibraryFragment)
            adapter.isNotDeleteMode = true
            groupView.adapter = adapter
        }

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

            createPlaylist(playlistName)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun createPlaylist(name:String){
        lifecycleScope.launch {
            var playlist = playlistDataHelper.insertPlaylist(name, userSession.userName!!)
            Log.d("CODE", "CODE: ${playlist}")
            userSession.addPlaylistId(playlist)
            val newPlaylist = Playlist(playlist, name, userSession.userName!!, R.drawable.games, null)
            adapter.addGroup(newPlaylist)
            adapter?.notifyDataSetChanged()
        }
    }

    private fun deletePlaylist(name:String){
        lifecycleScope.launch {
            val playlist = playlistList.firstOrNull { it.playlistName == name }
            val playlistID = playlist?.playlistId

            if (playlistID != null) {
                playlistDataHelper.removePlaylist(playlistID, userSession.userName!!)
                userSession.removePlaylistId(playlistID)
                adapter.delGroup(playlist)
                adapter?.notifyDataSetChanged()
            } else {
                Log.d("del playlist", "Not found.")
            }

        }
    }
}
