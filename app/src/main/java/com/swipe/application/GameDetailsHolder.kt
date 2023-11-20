package com.swipe.application

import android.content.Context
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import android.widget.MediaController
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GameDetailsHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val image: ImageView = itemView.findViewById(R.id.game_photo)
    private val gameName: TextView = itemView.findViewById(R.id.game_name)
    private val description: TextView = itemView.findViewById(R.id.game_description)
    private val videoId: VideoView = itemView.findViewById(R.id.game_vid)
    private val genresContainer: LinearLayout = itemView.findViewById(R.id.genresContainer2)
    private val platformList: TextView = itemView.findViewById(R.id.platform_list)
    private val price: TextView = itemView.findViewById(R.id.show_price)
    private val similarTitlesTextView: TextView = itemView.findViewById(R.id.similar_titles)
    private val similarTitlesContainer: LinearLayout = itemView.findViewById(R.id.similar_titles_container)
    private val popularPlayersTextView: TextView = itemView.findViewById(R.id.popular_players)
    private val popularPlayersContainer: LinearLayout = itemView.findViewById(R.id.popular_players_container)
    private val reviewsContainer: LinearLayout = itemView.findViewById(R.id.reviews_container)
    private val playButton: Button = itemView.findViewById(R.id.playButton)

    fun bindData(game: Games) {
        if(game.imageId!=0){
            image.setImageResource(game.imageId)
        } else{
            Glide.with(itemView.context)
                .load(game.imageURL)
                .into(image);

        }

        gameName.text = game.gameName
        description.text = game.description
        platformList.text = game.platform.toString()
        price.text = game.price
        playButton.setBackgroundColor(ContextCompat.getColor(this.context, R.color.orange))

        if(game.videoId !=0){
            videoId.setVideoURI(Uri.parse("android.resource://${itemView.context.packageName}/${game.videoId}"))
        }else{
            videoId.setVideoURI(Uri.parse(game.videoUrl))
        }
        val mediaController = MediaController(itemView.context)
        mediaController.setAnchorView(videoId)
        videoId.setMediaController(mediaController)

        val genreList = game.genre ?: emptyList()
        bindGenres(genreList)

        val addReview = AddReviewHolder(itemView)
        addReview.bindData()

        val similarTitlesList = game.similarGames ?: emptyList()
        bindSimilarTitles(similarTitlesList)

        val popularPlayersList = game.popularPlayers ?: emptyList()
        bindPopularPlayers(popularPlayersList)

        val reviewList = game.reviews ?: emptyList()
        bindReviews(reviewList)
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

    fun bindSimilarTitles(similarTitles: List<Games>) {
        if (similarTitles.isNotEmpty()) {
            similarTitlesContainer.visibility = View.VISIBLE
            similarTitlesTextView.visibility = View.VISIBLE

            similarTitlesContainer.removeAllViews()

            val inflater = LayoutInflater.from(itemView.context)

            for (similarTitle in similarTitles) {
                val similarTitleView = inflater.inflate(R.layout.game_or_user, similarTitlesContainer, false)
                val gameOrUserHolder = GameOrUserHolder(similarTitleView, null, null)

                gameOrUserHolder.bindData(similarTitle, true)

                similarTitlesContainer.addView(similarTitleView)
            }
        } else {
            similarTitlesContainer.visibility = View.GONE
            similarTitlesTextView.visibility = View.GONE
        }
    }

    fun bindPopularPlayers(popularPlayers: List<Users>) {
        if (popularPlayers.isNotEmpty()) {
            popularPlayersContainer.visibility = View.VISIBLE
            popularPlayersTextView.visibility = View.VISIBLE

            popularPlayersContainer.removeAllViews()

            val inflater = LayoutInflater.from(itemView.context)

            for (popularPlayer in popularPlayers) {
                val popularPlayerView = inflater.inflate(R.layout.game_or_user, popularPlayersContainer, false)
                val gameOrUserHolder = GameOrUserHolder(popularPlayerView, null, null)

                gameOrUserHolder.bindData(popularPlayer)

                popularPlayersContainer.addView(popularPlayerView)
            }
        } else {
            popularPlayersContainer.visibility = View.GONE
            popularPlayersTextView.visibility = View.GONE
        }
    }

    fun bindReviews(reviews: List<Reviews>) {
        if (reviews.isNotEmpty()) {
            reviewsContainer.visibility = View.VISIBLE

            reviewsContainer.removeAllViews()

            val inflater = LayoutInflater.from(itemView.context)

            for (review in reviews) {
                val reviewsView = inflater.inflate(R.layout.show_review, reviewsContainer, false)
                val reviewHolder = ShowReviewHolder(reviewsView)

                reviewHolder.bindData(review)

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.bottomMargin = 10.dpToPx(itemView.context)
                reviewsView.layoutParams = layoutParams

                reviewsContainer.addView(reviewsView)
            }
        } else {
            reviewsContainer.visibility = View.GONE
        }
    }

}
