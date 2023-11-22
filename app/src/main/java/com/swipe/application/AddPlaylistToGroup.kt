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
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.Serializable

class AddPlaylistToGroup : AppCompatActivity(), PlaylistActionListener {
    override fun onDeletePlaylistAction(name: String) {
    }

    // Define properties
    private lateinit var groupView: RecyclerView
    private var selectedImageUri: Uri? = null
    private lateinit var userSession: UserSession
    private lateinit var adapter: LibraryAdapter
    private lateinit var playlistList: ArrayList<Playlist>
    private val playlistDataHelper = PlaylistDataHelper()
    private val groupDataHelper = GroupDataHelper()
    private var isCreator = false
    private lateinit var groupDetails: Groups
    private lateinit var listener: PlaylistGameActionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.library)

        // Initialize views and other setup
        setupViews()
    }

    private fun setupViews() {
        // Initialize views
        groupView = findViewById(R.id.LibraryRecycler)
        groupView.layoutManager = LinearLayoutManager(this)

        userSession = UserSession(this)
        val playlists = userSession.playlist

        groupDetails = (intent.getBundleExtra("groupDetails")?.getSerializable("groupDetails") as? Groups)!!
        lifecycleScope.launch {
            var creator = groupDataHelper.getCreator(groupDetails.id)
            isCreator = creator == UserSession(this@AddPlaylistToGroup).userName
        }
        CoroutineScope(Dispatchers.Main).launch {
            playlistList = playlistDataHelper.retrievePlaylists(playlists)
            adapter = LibraryAdapter(playlistList, this@AddPlaylistToGroup) { clickedPlaylist ->
                Log.d("CLICKED PLAYLIST", "$clickedPlaylist")
                showAddConfirmationDialog(clickedPlaylist)
            }
            adapter.isNotDeleteMode = true
            groupView.adapter = adapter
        }

        val addButton: Button = findViewById(R.id.createButton)
        val delButton: Button = findViewById(R.id.deleteButton)

        addButton.visibility = View.INVISIBLE
        delButton.visibility = View.INVISIBLE
    }

    private fun showAddConfirmationDialog(playlist: Playlist) {
        val dialogView = layoutInflater.inflate(R.layout.remove_prompt, null)
        val tvConfirmMessage = dialogView.findViewById<TextView>(R.id.tvDeleteConfirm)
        tvConfirmMessage.text = "Are you sure you want to add ${playlist.playlistName}?"

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.btnConfirmYes).setOnClickListener {
            val returnIntent = Intent().apply {
                putExtra("returnedPlaylist", playlist) // 'playlist' is the value you want to return
            }

            setResult(Activity.RESULT_OK, returnIntent)
            alertDialog.dismiss()
            finish()
        }

        dialogView.findViewById<Button>(R.id.btnConfirmNo).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

}
