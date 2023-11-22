package com.swipe.application

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.UUID

class GroupDetailsActivity : AppCompatActivity(), PlaylistGameActionListener {
    companion object {
        const val PICK_IMAGE_REQUEST = 1
        const val ADD_PLAYLIST_REQUEST = 2
    }

    private lateinit var GroupNameOriginal: String
    private lateinit var GroupDescOriginal: String
    private lateinit var group: Groups
    private var isCreator = false
    private val groupDataHelper = GroupDataHelper()

    private lateinit var userRecyclerView: NonScrollableRecyclerView
    private lateinit var gameRecyclerView: NonScrollableRecyclerView
    private lateinit var playlistRecyclerView: NonScrollableRecyclerView

    private lateinit var userAdapter: GameOrUserAdapter
    private lateinit var gameAdapter: GameOrUserAdapter
    private lateinit var playlistAdapter: GameOrUserAdapter

    override fun onAddPlaylistGameAction(game: Games) {
        lifecycleScope.launch {
            groupDataHelper.addGameToGroup(group.id, game)
            gameAdapter.addGameToPlaylist(game)
        }
    }

    override fun onDeletePlaylistGameAction(game: Games) {
        lifecycleScope.launch {
            groupDataHelper.removeGameFromGroup(group.id, game)
            gameAdapter.removeGameToPlaylist(game)
        }
    }

    override fun onAddPlaylistAction(playlist: Playlist) {
        lifecycleScope.launch {
            groupDataHelper.insertPlaylist(group.id, playlist.playlistId)
            playlistAdapter.addPlaylistfromGroup(playlist)
        }
    }

    override fun onDeletePlaylistAction(playlist: Playlist) {
        lifecycleScope.launch {
            groupDataHelper.removePlaylist(group.id, playlist.playlistId)
            playlistAdapter.removePlaylistfromGroup(playlist)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group_details)

        group = intent.getSerializableExtra("GroupDetails") as Groups

        val gameItemClickListener: (Any) -> Unit = { item ->
            Log.d("ITEM CONTENTS", "$item")
            if (item is Games) {
                val intent = Intent(this, GameDetailsActivity::class.java).apply {
                    intent.putExtra("gameDetails", item as Serializable)
                }
                startActivity(intent)
            } else if (item is Playlist) {
                val intent = Intent(this, PlaylistDetailsActivity::class.java).apply {
                    val playlistDetailsBundle = Bundle().apply {
                        putSerializable("playlistDetails", item)
                    }
                    intent.putExtra("playlistDetails", playlistDetailsBundle)
                }
                startActivity(intent)
            } else {
                Log.e("GameItemClickListener", "Item is not of type Game")
            }
        }

        val image: ImageView = findViewById(R.id.icon)
        val uploadPhotoButton: Button = findViewById(R.id.upload_photo_btn)

        val groupName: TextView = findViewById(R.id.group_text)
        val saveNameContainer: LinearLayout = findViewById(R.id.save_name_container)
        val saveTitle: Button = findViewById(R.id.save_btn1)
        val cancelTitle: Button = findViewById(R.id.cancel_btn1)

        val username: TextView = findViewById(R.id.username_text)
        val number: TextView = findViewById(R.id.num_users)
        val code: TextView = findViewById(R.id.code_text)

        val desc: TextView = findViewById(R.id.description)
        val saveDescContainer: LinearLayout = findViewById(R.id.save_name_container)
        val saveDesc: Button = findViewById(R.id.save_btn2)
        val cancelDesc: Button = findViewById(R.id.cancel_btn2)

        val addGame: Button = findViewById(R.id.add_button1)
        val delGame: Button = findViewById(R.id.del_button1)
        val addPlaylist: Button = findViewById(R.id.add_button2)
        val delPlaylist: Button = findViewById(R.id.del_button2)

        val backButton: Button = findViewById(R.id.backButton)

        userRecyclerView = findViewById(R.id.GroupRecycler1)
        gameRecyclerView = findViewById(R.id.GroupRecycler2)
        playlistRecyclerView = findViewById(R.id.GroupRecycler3)

        GroupNameOriginal = group.name.trim()
        GroupDescOriginal = group.desc

        groupName.text = group.name
        desc.text = group.desc
        number.text = "${group.count} users"
        username.text = group.creator
        code.text = group.id

        saveNameContainer.visibility = View.INVISIBLE
        saveTitle.visibility = View.INVISIBLE
        cancelTitle.visibility = View.INVISIBLE

        lifecycleScope.launch {
            val creator = groupDataHelper.getCreator(group.id)
            isCreator = creator == UserSession(this@GroupDetailsActivity).userName

            var gameList = groupDataHelper.returnGames(group.id)
            gameAdapter = GameOrUserAdapter(gameList.toMutableList() ?: mutableListOf(),  gameItemClickListener,this@GroupDetailsActivity)

            userAdapter = GameOrUserAdapter(group.users?.toMutableList() ?: mutableListOf(),  gameItemClickListener,this@GroupDetailsActivity)

            var playlistList = PlaylistDataHelper().retrievePlaylists(group.playlists.toSet())
            playlistAdapter = GameOrUserAdapter(playlistList.toMutableList() ?: mutableListOf(),  gameItemClickListener,this@GroupDetailsActivity)

            gameAdapter.isNotDeleteMode = true
            userAdapter.isNotDeleteMode = true
            playlistAdapter.isNotDeleteMode = true

            userRecyclerView.layoutManager = GridLayoutManager(this@GroupDetailsActivity, 3)
            gameRecyclerView.layoutManager = GridLayoutManager(this@GroupDetailsActivity, 3)
            playlistRecyclerView.layoutManager = GridLayoutManager(this@GroupDetailsActivity, 3)

            userRecyclerView.adapter = userAdapter
            gameRecyclerView.adapter = gameAdapter
            playlistRecyclerView.adapter = playlistAdapter

            addPlaylist.setOnClickListener {
                val intent = Intent(this@GroupDetailsActivity, AddPlaylistToGroup::class.java)
                val groupDetailsBundle = Bundle().apply {
                    putSerializable("groupDetails", group)
                }
                intent.putExtra("groupDetails", groupDetailsBundle)
                startActivityForResult(intent, ADD_PLAYLIST_REQUEST)
            }

            delPlaylist.setOnClickListener {
                delPlaylist.isSelected = !delPlaylist.isSelected
                playlistAdapter.isNotDeleteMode = !delPlaylist.isSelected
                playlistAdapter.notifyDataSetChanged()
            }

            code.setOnClickListener {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", group.id)
                clipboard.setPrimaryClip(clip)
                showCustomToast("Text copied to clipboard")
            }

            addGame.setOnClickListener {
                val intent = Intent(this@GroupDetailsActivity, AddGamesToPlaylist::class.java)
                val groupDetailsBundle = Bundle().apply {
                    putSerializable("groupDetails", group)
                }
                intent.putExtra("groupDetails", groupDetailsBundle)
                startActivityForResult(intent, 3)
            }

            delGame.setOnClickListener {
                delGame.isSelected = !delGame.isSelected
                gameAdapter.isNotDeleteMode = !delGame.isSelected
                gameAdapter.notifyDataSetChanged()
            }

            backButton.setOnClickListener {
                val returnIntent = Intent().apply {
                    putExtra("returnedGroup", group)
                }

                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }

            if (group.image != "") {
                Glide.with(this@GroupDetailsActivity)
                    .load(group.image)
                    .into(image)
            } else {
                image.setImageResource(R.drawable.groups)
            }

            if (!isCreator) {
                groupName.isEnabled = false
                uploadPhotoButton.visibility = View.INVISIBLE
                saveNameContainer.visibility = View.INVISIBLE
                desc.isEnabled = false
                saveDescContainer.visibility = View.INVISIBLE

            } else {
                groupName.isEnabled = true
                uploadPhotoButton.visibility = View.VISIBLE
                desc.isEnabled = true
                saveDescContainer.visibility = View.VISIBLE

                groupName.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(editable: Editable?) {
                        if (editable.toString().trim() != GroupNameOriginal) {
                            saveNameContainer.visibility = View.VISIBLE
                        } else {
                            saveNameContainer.visibility = View.INVISIBLE
                        }
                    }
                })

                saveTitle.setOnClickListener {
                    val newName = groupName.text.toString().trim()
                    if (newName != ""){
                        GroupNameOriginal = newName
                        lifecycleScope.launch {
                            groupDataHelper.updateGroupName(group.id, GroupNameOriginal)
                        }
                    } else {
                        showCustomToast("Group Name should not be empty")
                    }
                    saveNameContainer.visibility = View.INVISIBLE
                }

                cancelTitle.setOnClickListener {
                    groupName.text = GroupNameOriginal
                    saveNameContainer.visibility = View.INVISIBLE
                }

                desc.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(editable: Editable?) {
                        if (editable.toString() != GroupNameOriginal) {
                            saveDescContainer.visibility = View.VISIBLE
                        } else {
                            saveDescContainer.visibility = View.GONE
                        }
                    }
                })

                saveDesc.setOnClickListener {
                    val newDesc = desc.text.toString().trim()
                    GroupDescOriginal = newDesc
                    lifecycleScope.launch {
                        groupDataHelper.updateGroupDesc(group.id, GroupDescOriginal)
                    }
                    saveDescContainer.visibility = View.INVISIBLE
                }

                cancelDesc.setOnClickListener {
                    desc.text = GroupDescOriginal
                    saveDescContainer.visibility = View.INVISIBLE
                }

                uploadPhotoButton.setOnClickListener {
                    openGalleryForImage()
                }
            }
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val storageReference = FirebaseStorage.getInstance().getReference()
        Log.d("onActivityResult", "$requestCode and $resultCode")
        if (requestCode == PlaylistDetailsActivity.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                Log.d("URI", "$uri")
                val imageRef = storageReference.child("images/playlists/${UUID.randomUUID()}")
                imageRef.putFile(uri)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            lifecycleScope.launch {
                                groupDataHelper.updateGroupImage(group.id, downloadUri.toString().trim())

                                Glide.with(this@GroupDetailsActivity)
                                    .load(downloadUri.toString().trim())
                                    .into(findViewById(R.id.icon))
                            }
                        }
                    }
                    .addOnFailureListener {
                    }
            }
        } else if (requestCode == ADD_PLAYLIST_REQUEST && resultCode == Activity.RESULT_OK) {
            val returnedPlaylist = data?.getSerializableExtra("returnedPlaylist") as? Playlist
            if (returnedPlaylist != null) {
                onAddPlaylistAction(returnedPlaylist)
            }
        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            val returnedGame = data?.getSerializableExtra("returnedGame") as? Games
            if (returnedGame != null) {
                onAddPlaylistGameAction(returnedGame)
            }
        }
    }

    private fun showCustomToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.toast_container))

        val textView: TextView = layout.findViewById(R.id.toast_text)
        textView.text = message

        with (Toast(applicationContext)) {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }
}
