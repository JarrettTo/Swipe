package com.swipe.application

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserDataHelper {

    private val databaseReference = FirebaseDatabase.getInstance().getReference("test")
    private val usersBranch = databaseReference.child("users")

    fun createUser(username: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit, onUserExists: () -> Unit) {
        usersBranch.equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val user = userSnapshot.getValue(Users::class.java)
                            if (user != null && user.password == password) {
                                onUserExists()
                                return
                            }
                        }
                    } else {
                        usersBranch.child(username).child("password").setValue(password)
                        onSuccess()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure()
                }
            })
    }

    fun authenticateUser(username: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        usersBranch.equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val user = userSnapshot.getValue(Users::class.java)
                            if (user != null && user.password == password) {
                                onSuccess()
                                return
                            }
                        }
                        onFailure("Incorrect password")
                    } else {
                        onFailure("This user does not exist")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure("Database error: ${error.message}")
                }
            })
    }

    suspend fun getAllUsers(): List<Users> = withContext(Dispatchers.IO) {
        val usersList = mutableListOf<Users>()

        try {
            val snapshot = usersBranch.get().await()
            if (snapshot.exists()) {
                snapshot.children.forEach { childSnapshot ->
                    val username = childSnapshot.key ?: ""
                    val profile = childSnapshot.child("profile").getValue(Int::class.java) ?: 0
                    val password = childSnapshot.child("password").getValue(String::class.java) ?: ""
                    val profileURL = childSnapshot.child("profileURL").getValue(String::class.java)

                    val user = Users(username, profile, password, profileURL)
                    usersList.add(user)
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error fetching all users", e)
        }

        return@withContext usersList
    }


    suspend fun getUsersByUsernames(usernames: Set<String>?): List<Users> = withContext(Dispatchers.IO) {
        val usersList = mutableListOf<Users>()

        try {
            usernames?.forEach { username ->
                val userSnapshot = usersBranch.child(username).get().await()
                if (userSnapshot.exists()) {
                    val profile = userSnapshot.child("profile").getValue(Int::class.java) ?: 0
                    val password = userSnapshot.child("password").getValue(String::class.java) ?: ""
                    val profileURL = userSnapshot.child("profileURL").getValue(String::class.java)

                    val user = Users(username, profile, password, profileURL)
                    usersList.add(user)
                } else {
                    Log.d("FirebaseInfo", "User with username: $username does not exist.")
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error fetching users by usernames", e)
        }

        return@withContext usersList
    }
}
