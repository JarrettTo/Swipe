package com.swipe.application

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShowReviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val userIcon: ImageView = itemView.findViewById(R.id.icon)
    private val userName: TextView = itemView.findViewById(R.id.name)
    private val star1: ImageView = itemView.findViewById(R.id.star1)
    private val star2: ImageView = itemView.findViewById(R.id.star2)
    private val star3: ImageView = itemView.findViewById(R.id.star3)
    private val star4: ImageView = itemView.findViewById(R.id.star4)
    private val star5: ImageView = itemView.findViewById(R.id.star5)
    private val reviewDescription: TextView = itemView.findViewById(R.id.review)

    fun bindData(review: Reviews) {
        userIcon.setImageResource(review.user.profile)
        userName.text = review.user.username
        reviewDescription.text = review.description

        setStarImages(review.rating)
    }

    private fun setStarImages(rating: Int) {
        val starImageViews = listOf(star1, star2, star3, star4, star5)

        for (i in 0 until starImageViews.size) {
            val starImageView = starImageViews[i]
            if (i < rating) {
                starImageView.setImageResource(R.drawable.filled_star)
            } else {
                starImageView.setImageResource(R.drawable.unfilled_star)
            }
        }
    }
}
