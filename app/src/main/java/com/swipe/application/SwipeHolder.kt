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
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide


class SwipeHolder(itemView: View) : ViewHolder(itemView) {
    private val image: ImageView = itemView.findViewById(R.id.photoBackground)
    private val gameName: TextView = itemView.findViewById(R.id.title)
    private val description: TextView = itemView.findViewById(R.id.description)
    private val videoId: VideoView = itemView.findViewById(R.id.gameplayVideo)
    private val genresContainer: LinearLayout = itemView.findViewById(R.id.genresContainer)

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

        val genreList = game.genre ?: emptyList()

        val mediaController = MediaController(itemView.context)
        mediaController.setAnchorView(videoId)
        videoId.setMediaController(mediaController)
        if(game.videoId !=0){
            videoId.setVideoURI(Uri.parse("android.resource://${itemView.context.packageName}/${game.videoId}"))
        }else{
            Log.d("URL", "THIS IS: ${game.videoUrl}")
            videoId.setVideoURI(Uri.parse(game.videoUrl))

        }

        bindGenres(genreList)

        val nestedScrollView = itemView.findViewById<NestedScrollView>(R.id.nestedScrollView)
        val darkOverlay = itemView.findViewById<View>(R.id.darkOverlay)

        nestedScrollView.post {
            nestedScrollView.scrollTo(0, 100)
        }

        nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val totalScrollRange = nestedScrollView.getChildAt(0).height - nestedScrollView.height
            val alpha = (scrollY.toFloat() / totalScrollRange).coerceIn(0.5f, 1.0f)
            darkOverlay.alpha = alpha
        }
    }


    private fun bindGenres(genres: List<String>) {
        genresContainer.removeAllViews()

        val inflater = LayoutInflater.from(itemView.context)
        val oblongShapeLayout = R.layout.genres

        val maxWidth = calculateMaxWidthForGenres()

        var totalWidth = 0
        for ((index, genre) in genres.withIndex()) {
            val oblongShapeView = inflater.inflate(oblongShapeLayout, null, false)
            val genreTextView: TextView = oblongShapeView.findViewById(R.id.genreTextView)
            genreTextView.text = genre
            oblongShapeView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val genreWidth = oblongShapeView.measuredWidth

            val spaceWidth = if (index > 0) 4.dpToPx(itemView.context) else 0

            if (totalWidth + genreWidth + spaceWidth > maxWidth) {
                Log.d("BindGenres", "Would be size: ${totalWidth + genreWidth + spaceWidth}")
                break
            }

            if (index > 0) {
                val space = View(itemView.context)
                space.layoutParams = LinearLayout.LayoutParams(spaceWidth, 1.dpToPx(itemView.context))
                genresContainer.addView(space)
                totalWidth += spaceWidth
            }

            genresContainer.addView(oblongShapeView)
            totalWidth += genreWidth

            Log.d("BindGenres", "Total width after adding genre [$index]: $genre, width: $totalWidth")
        }
    }


    private fun calculateMaxWidthForGenres(): Int {
        return 500
    }

    fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

}
