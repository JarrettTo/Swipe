package com.swipe.application

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ReviewDataHelper {

    suspend fun insertReview(gameId: Int, user: Users, rating: Int, description: String): String = withContext(
        Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val reviewsRef = dbRef.child("reviews")
        val newReviewRef = reviewsRef.push()

        try {
            val review = Reviews("", user, gameId, rating, description)
            newReviewRef.setValue(review)

            return@withContext newReviewRef.key.toString()
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error inserting review", e)
            return@withContext "error"
        }
    }

    suspend fun retrieveReviews(gameID: Int): List<Reviews> = withContext(Dispatchers.IO) {
        val reviews = mutableListOf<Reviews>()
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val reviewsRef = dbRef.child("reviews")

        try {
            val snapshot = reviewsRef.get().await()
            for (reviewSnapshot in snapshot.children) {
                var review = reviewSnapshot.getValue<Reviews>()
                if (review != null && review.gameId == gameID) {
                    review.reviewID = reviewSnapshot.key.toString()
                    reviews.add(0, review)
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error fetching reviews", e)
        }

        return@withContext reviews
    }

    suspend fun retrieveAllReviewsForUserAndGame(gameID: Int, username: String): List<Reviews> = withContext(Dispatchers.IO) {
        val reviews = mutableListOf<Reviews>()
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val reviewsRef = dbRef.child("reviews")

        try {
            val snapshot = reviewsRef.get().await()
            for (reviewSnapshot in snapshot.children) {
                var review = reviewSnapshot.getValue<Reviews>()
                if (review != null && review.gameId == gameID && review.user.username == username) {
                    review.reviewID = reviewSnapshot.key.toString()
                    reviews.add(review)
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error fetching reviews", e)
        }

        return@withContext reviews
    }

    suspend fun deleteReview(reviewID: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val reviewRef = dbRef.child("reviews").child(reviewID)

        try {
            reviewRef.removeValue().await()
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error deleting review", e)
            return@withContext false
        }
    }

}