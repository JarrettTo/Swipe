package com.swipe.application

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddReviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val userIcon: ImageView = itemView.findViewById(R.id.icon)
    private val userName: TextView = itemView.findViewById(R.id.name)
    private val star1: ImageView = itemView.findViewById(R.id.star1)
    private val star2: ImageView = itemView.findViewById(R.id.star2)
    private val star3: ImageView = itemView.findViewById(R.id.star3)
    private val star4: ImageView = itemView.findViewById(R.id.star4)
    private val star5: ImageView = itemView.findViewById(R.id.star5)
    private val iconComment: ImageView = itemView.findViewById(R.id.iconComment)

    fun bindData(){
        this.bindStars()

        //temp data
        userIcon.setImageResource(R.drawable.karltzy)
        userName.text = "XxTheLegendxX"

        iconComment.setOnClickListener {
            Log.d("GameDetailsHolder", "iconComment Clicked")
        }
    }

    fun bindStars() {
        val starImageViews = listOf(star1, star2, star3, star4, star5)

        for (i in starImageViews.indices) {
            val starImageView = starImageViews[i]
            starImageView.setOnClickListener {
                // Handle star click
                updateStarImages(i + 1)
            }
        }
    }

    private fun updateStarImages(clickedStarPosition: Int) {
        val starImageViews = listOf(star1, star2, star3, star4, star5)

        for (i in starImageViews.indices) {
            val starImageView = starImageViews[i]

            // Determine whether to set filled or unfilled star based on the position
            val isFilled = i < clickedStarPosition

            if (isFilled) {
                starImageView.setImageResource(R.drawable.filled_star)
            } else {
                starImageView.setImageResource(R.drawable.unfilled_star)
            }
        }
    }
}
