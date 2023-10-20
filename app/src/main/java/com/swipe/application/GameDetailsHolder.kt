package com.swipe.application

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.VideoView
import android.widget.Button
import android.widget.MediaController
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class GameDetailsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val image: ImageView = itemView.findViewById(R.id.game_photo)
    private val gameName: TextView = itemView.findViewById(R.id.game_name)
    private val description: TextView = itemView.findViewById(R.id.game_description)
    private val videoId: VideoView = itemView.findViewById(R.id.game_vid)
    private val genresContainer: LinearLayout = itemView.findViewById(R.id.genresContainer2)
    private val platformList: TextView = itemView.findViewById(R.id.platform_list)
    private val price: TextView = itemView.findViewById(R.id.show_price)
    private val similarTitlesContainer: LinearLayout = itemView.findViewById(R.id.similar_titles_container)
    private val popularPlayersContainer: LinearLayout = itemView.findViewById(R.id.popular_players_container)
    private val reviewsContainer: LinearLayout = itemView.findViewById(R.id.reviews_container)

    fun bindData(game: Games) {
        image.setImageResource(game.imageId)
        gameName.text = game.gameName
        description.text = game.description
        platformList.text = game.platform.toString()
        price.text = game.price

        val genreList = game.genre ?: emptyList()

        videoId.setVideoURI(Uri.parse("android.resource://${itemView.context.packageName}/${game.videoId}"))

        val mediaController = MediaController(itemView.context)
        mediaController.setAnchorView(videoId)
        videoId.setMediaController(mediaController)

        bindGenres(genreList)
    }


    private fun bindGenres(genres: List<String>) {
        genresContainer.removeAllViews()

        val inflater = LayoutInflater.from(itemView.context)
        val oblongShapeLayout = R.layout.genres

        for ((index, genre) in genres.withIndex()) {
            val oblongShapeView = inflater.inflate(oblongShapeLayout, genresContainer, false)
            val genreTextView = oblongShapeView.findViewById<TextView>(R.id.genreTextView)
            genreTextView.text = genre

            if (index > 0) {
                // Add a transparent spacer view
                val space = View(itemView.context)
                space.layoutParams = LinearLayout.LayoutParams(4.dpToPx(itemView.context), 1.dpToPx(itemView.context))
                genresContainer.addView(space)
            }

            genresContainer.addView(oblongShapeView)
        }
    }

    fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

}
