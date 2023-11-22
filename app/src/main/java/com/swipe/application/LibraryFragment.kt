package com.swipe.application

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.io.Serializable

interface PlaylistActionListener {
    fun onDeletePlaylistAction(name: String)
}

class LibraryFragment : Fragment() , PlaylistActionListener{
    override fun onDeletePlaylistAction(name: String) {
        deletePlaylist(name)
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            lifecycleScope.launch {
                val playlists = userSession.playlist
                playlistList = playlistDataHelper.retrievePlaylists(playlists)
                adapter.updateData(playlistList)
                adapter.notifyDataSetChanged()
            }
        }
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

        lifecycleScope.launch {
            val playlists = playlistDataHelper.retrieveUserPlaylist(userSession.userName)
            Log.d("PLAYLIST-IDS:", "CHECK ${playlists}")

            playlistList = playlistDataHelper.retrievePlaylists(playlists)
            Log.d("PLAYLIST:", "CHECK ${playlistList}")

            val likedGamesIndex = playlistList.indexOfFirst { it.playlistName == "Liked Games" }
            if (likedGamesIndex != -1) {
                val likedGamesPlaylist = playlistList.removeAt(likedGamesIndex)
                playlistList.add(0, likedGamesPlaylist)
            }

            adapter = LibraryAdapter(playlistList, this@LibraryFragment) { clickedPlaylist ->
                Log.d("CLICKED PLAYLIST", "$clickedPlaylist")
                val intent = Intent(context, PlaylistDetailsActivity::class.java)
                val playlistDetailsBundle = Bundle().apply {
                    putSerializable("playlistDetails", clickedPlaylist)
                }
                intent.putExtra("playlistDetails", playlistDetailsBundle)

                startActivity(intent)
            }
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
            if (playlistName != "") {
                lifecycleScope.launch {
                    val playlists = playlistDataHelper.retrieveUserPlaylist(userSession.userName)
                    val existingPlaylists = playlistDataHelper.retrievePlaylists(playlists)
                    val playlistExists = existingPlaylists.any { it.playlistName == playlistName }

                    if (!playlistExists) {
                        val playlistId = playlistDataHelper.insertPlaylist(playlistName, userSession.userName!!)
                        Log.d("CODE", "CODE: $playlistId")

                        userSession.addPlaylistId(playlistId)
                        val newPlaylist = Playlist(playlistId, playlistName, userSession.userName!!, R.drawable.games, "", null)
                        adapter.addPlaylist(newPlaylist)
                        adapter.notifyDataSetChanged()

                        alertDialog.dismiss()
                    } else {
                        showCustomToast("Playlist with name $playlistName already exists.")
                    }
                }
            } else {
                showCustomToast("Playlist Name should not be empty")
            }
        }

        alertDialog.show()
    }

    private fun deletePlaylist(name:String){
        lifecycleScope.launch {
            val playlist = playlistList.firstOrNull { it.playlistName == name }
            val playlistID = playlist?.playlistId

            if (playlistID != null) {
                playlistDataHelper.removePlaylist(playlistID, userSession.userName!!)
                userSession.removePlaylistId(playlistID)
                adapter.delPlaylist(playlist)
                adapter?.notifyDataSetChanged()
            } else {
                Log.d("del playlist", "Not found.")
            }

        }
    }

    private fun showCustomToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, requireActivity().findViewById(R.id.toast_container))

        val textView: TextView = layout.findViewById(R.id.toast_text)
        textView.text = message

        with (Toast(requireContext())) {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }
}
