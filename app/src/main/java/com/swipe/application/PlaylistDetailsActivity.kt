package com.swipe.application

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.UUID

interface PlaylistGameActionListener {
    fun onAddPlaylistGameAction(games: Games)
    fun onDeletePlaylistGameAction(games: Games)

    fun onAddPlaylistAction(playlist: Playlist)
    fun onDeletePlaylistAction(playlist: Playlist)
}

class PlaylistDetailsActivity : AppCompatActivity(), PlaylistGameActionListener {
    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    private lateinit var recyclerView: RecyclerView
    private val playlistDataHelper = PlaylistDataHelper()
    private lateinit var userSession: UserSession
    private lateinit var playlistDetails: Playlist
    private lateinit var PlaylistNameOriginal: String
    private lateinit var gameOrUserAdapter: GameOrUserAdapter

    override fun onAddPlaylistGameAction(game: Games) {
        lifecycleScope.launch {
            if (playlistDataHelper.isGameAlreadyInPlaylist(playlistDetails.playlistId, game)){
                showCustomToast("Game is already in playlist")
            } else {
                playlistDataHelper.addGameToPlaylist(playlistDetails.playlistId, game)
                gameOrUserAdapter.addGameToPlaylist(game)
                findViewById<TextView>(R.id.num_games).text =
                    "${(playlistDetails.games?.size ?: 0) + 1} games"
            }
        }
    }

    override fun onDeletePlaylistGameAction(game: Games) {
        removeGameToPlaylist(game)
    }

    override fun onAddPlaylistAction(playlist: Playlist) {
    }

    override fun onDeletePlaylistAction(playlist: Playlist) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inside_playlist)

        playlistDetails = (intent.getBundleExtra("playlistDetails")?.getSerializable("playlistDetails") as? Playlist)!!

        // Set the playlist details
        val image: ImageView = findViewById(R.id.icon)
        val uploadPhotoButton: Button = findViewById(R.id.upload_photo_btn)
        val playlistName: TextView = findViewById(R.id.playlist_text)
        val username: TextView = findViewById(R.id.username_text)
        val number: TextView = findViewById(R.id.num_games)
        val addButton: Button = findViewById(R.id.add_button)
        val delButton: Button = findViewById(R.id.del_button)
        val backButton: Button = findViewById(R.id.backButton)
        val saveContainer: LinearLayout = findViewById(R.id.save_name_container)
        val saveButton: Button = findViewById(R.id.save_btn)
        val cancelButton: Button = findViewById(R.id.cancel_btn)
        val gameItemClickListener: (Any) -> Unit = { item ->
            if (item is Games) {
                val intent = Intent(this, GameDetailsActivity::class.java).apply {
                    putExtra("gameDetails", item as Serializable)
                }
                startActivity(intent)
            } else {
                Log.e("GameItemClickListener", "Item is not of type Game")
            }
        }

        userSession = UserSession(this)

        if (playlistDetails.username != userSession.userName){
            uploadPhotoButton.visibility = View.INVISIBLE
            addButton.visibility = View.INVISIBLE
            delButton.visibility = View.INVISIBLE
            playlistName.isEnabled = false
        }

        if (playlistDetails.imageURL != "") {
            Glide.with(this)
                .load(playlistDetails.imageURL)
                .into(image)
        } else {
            playlistDetails.imageId?.let { image.setImageResource(R.drawable.games) }
        }

        playlistName.text = playlistDetails.playlistName
        PlaylistNameOriginal = playlistDetails.playlistName
        username.text = playlistDetails.username
        number.text = "${playlistDetails.games?.size ?: 0} games"

        recyclerView = findViewById(R.id.PlaylistRecycler)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        gameOrUserAdapter = GameOrUserAdapter(playlistDetails.games?.toMutableList() ?: mutableListOf(),  gameItemClickListener,this)
        recyclerView.adapter = gameOrUserAdapter
        gameOrUserAdapter.isNotDeleteMode = true

        backButton.setOnClickListener {
            onBackPressed()
        }

        addButton.setOnClickListener {
            val intent = Intent(this, AddGamesToPlaylist::class.java)
            val playlistDetailsBundle = Bundle().apply {
                putSerializable("playlistDetails", playlistDetails)
            }
            intent.putExtra("playlistDetails", playlistDetailsBundle)
            startActivityForResult(intent, 3)
        }

        delButton.setOnClickListener {
            delButton.isSelected = !delButton.isSelected
            gameOrUserAdapter.isNotDeleteMode = !delButton.isSelected
            gameOrUserAdapter.notifyDataSetChanged()
        }

        uploadPhotoButton.setOnClickListener {
            openGalleryForImage()
        }

        if (playlistName.text.toString().trim() == "Liked Games") {
            playlistName.isEnabled = false
        } else {
            playlistName.isEnabled = true

            playlistName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(editable: Editable?) {
                    if (editable.toString() != PlaylistNameOriginal) {
                        findViewById<LinearLayout>(R.id.save_name_container).visibility = View.VISIBLE
                    } else {
                        findViewById<LinearLayout>(R.id.save_name_container).visibility = View.GONE
                    }
                }
            })
        }

        saveButton.setOnClickListener {
            val newName = playlistName.text.toString().trim()
            if (newName != ""){
                PlaylistNameOriginal = newName
                lifecycleScope.launch {
                    playlistDataHelper.updatePlaylistName(playlistDetails.playlistId, PlaylistNameOriginal)
                }
            } else {
                showCustomToast("Playlist Name should not be empty")
            }
            saveContainer.visibility = View.INVISIBLE
        }

        cancelButton.setOnClickListener {
            playlistName.text = PlaylistNameOriginal
            saveContainer.visibility = View.INVISIBLE
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
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                Log.d("URI", "$uri")
                val imageRef = storageReference.child("images/playlists/${UUID.randomUUID()}")
                imageRef.putFile(uri)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            lifecycleScope.launch {
                                playlistDataHelper.updatePlaylistImage(playlistDetails.playlistId, downloadUri.toString().trim())

                                Glide.with(this@PlaylistDetailsActivity)
                                    .load(downloadUri.toString().trim())
                                    .into(findViewById(R.id.icon))
                            }
                        }
                    }
                    .addOnFailureListener {
                    }
            }
        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            val returnedGame = data?.getSerializableExtra("returnedGame") as? Games
            if (returnedGame != null) {
                onAddPlaylistGameAction(returnedGame)
            }
        }
    }

    private fun removeGameToPlaylist(game: Games) {
        lifecycleScope.launch {
            playlistDetails.games?.let {
                it.remove(game)
                findViewById<TextView>(R.id.num_games).text = "${it.size} games"
            }

            gameOrUserAdapter.removeGameToPlaylist(game)
            playlistDataHelper.removeGameFromPlaylist(playlistDetails.playlistId, game)
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
class NonScrollableRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST))
    }
}

