package com.swipe.application

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlaylistDetailsActivity : AppCompatActivity() {
    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inside_playlist)

        val playlistDetails = intent.getBundleExtra("playlistDetails")?.getSerializable("playlistDetails") as? Playlist
        if (playlistDetails == null) {
            // Handle the case where there are no playlist details
            return
        }

        // Set the playlist details
        val image: ImageView = findViewById(R.id.icon)
        val uploadPhotoButton: Button = findViewById(R.id.upload_photo_btn)
        val playlistName: TextView = findViewById(R.id.playlist_text)
        val username: TextView = findViewById(R.id.username_text)
        val number: TextView = findViewById(R.id.num_games)
        val addButton: Button = findViewById(R.id.add_button)
        val delButton: Button = findViewById(R.id.del_button)
        val backButton: Button = findViewById(R.id.backButton)

        playlistDetails.imageId?.let { image.setImageResource(it) }

        playlistName.text = playlistDetails.playlistName
        username.text = playlistDetails.username
        number.text = "${playlistDetails.games?.size ?: 0} games"

        recyclerView = findViewById(R.id.PlaylistRecycler)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val gameOrUserAdapter = GameOrUserAdapter(playlistDetails.games ?: emptyList())
        recyclerView.adapter = gameOrUserAdapter
        gameOrUserAdapter.isNotDeleteMode = true

        backButton.setOnClickListener {
            onBackPressed()
        }

        addButton.setOnClickListener {
            val intent = Intent(this, AddGamesToPlaylist::class.java)
            startActivity(intent)
        }

        delButton.setOnClickListener {
            delButton.isSelected = !delButton.isSelected
            gameOrUserAdapter.isNotDeleteMode = !delButton.isSelected
            gameOrUserAdapter.notifyDataSetChanged()
        }

        uploadPhotoButton.setOnClickListener {
            openGalleryForImage()
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            // CALL DB

            val imageView: ImageView = findViewById(R.id.icon)
            imageUri?.let {
                Glide.with(this).load(it).into(imageView)
            }
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

