package com.swipe.application

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class AddReviewHolder(itemView: View, private val gameID: Int, private val lifecycleScope: LifecycleCoroutineScope, private val listener: GameDetailsListener) : RecyclerView.ViewHolder(itemView) {
    private val userIcon: ImageView = itemView.findViewById(R.id.icon)
    private val userName: TextView = itemView.findViewById(R.id.name)
    private val star1: ImageView = itemView.findViewById(R.id.star1)
    private val star2: ImageView = itemView.findViewById(R.id.star2)
    private val star3: ImageView = itemView.findViewById(R.id.star3)
    private val star4: ImageView = itemView.findViewById(R.id.star4)
    private val star5: ImageView = itemView.findViewById(R.id.star5)
    private val review: TextView = itemView.findViewById(R.id.addReview)
    private val iconComment: ImageView = itemView.findViewById(R.id.iconComment)
    private var currentRating = 5
    private val reviewDataHelper = ReviewDataHelper()
    private val userDataHelper = UserDataHelper()
    private lateinit var userSession: UserSession

    fun bindData(){
        this.bindStars()

        lifecycleScope.launch {
            userSession = UserSession(itemView.context)
            var user = userSession.userName?.let { userDataHelper.getUserByUsername(it) }!!
            var profileURL = user.profileURL

            if (profileURL != "") {
                Glide.with(itemView.context)
                    .load(profileURL)
                    .placeholder(R.drawable.dp)
                    .error(R.drawable.dp)
                    .into(userIcon);
            } else {
                userIcon.setImageResource(R.drawable.dp)
            }

            userName.text = UserSession(itemView.context).userName
        }

        iconComment.setOnClickListener {
            val reviewText = review.text.toString()
            if (reviewText.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = userSession.userName?.let { userDataHelper.getUserByUsername(it) }!!
                    reviewDataHelper.insertReview(gameID, user, currentRating, reviewText)
                    listener.onReviewUpdated()
                    review.setText("")
                }
            } else {
                showCustomToast("Please write a review")
            }
        }
    }

    fun bindStars() {
        val starImageViews = listOf(star1, star2, star3, star4, star5)

        for (i in starImageViews.indices) {
            val starImageView = starImageViews[i]
            starImageView.setOnClickListener {
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

        currentRating = clickedStarPosition
    }

    private fun showCustomToast(message: String) {
        val inflater = LayoutInflater.from(itemView.context)
        val layout = inflater.inflate(R.layout.custom_toast, null)
        val textView: TextView = layout.findViewById(R.id.toast_text)
        textView.text = message

        with(Toast(itemView.context)) {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }
}
