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
                    val firstname = childSnapshot.child("firstname").getValue(String::class.java) ?: ""
                    val lastname = childSnapshot.child("lastname").getValue(String::class.java) ?: ""
                    val bio = childSnapshot.child("bio").getValue(String::class.java) ?: ""
                    val password = childSnapshot.child("password").getValue(String::class.java) ?: ""
                    val profileURL = childSnapshot.child("imageURL").getValue(String::class.java)

                    val user = Users(username, firstname, lastname, bio, profile, password, profileURL)
                    usersList.add(user)
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error fetching all users", e)
        }

        return@withContext usersList
    }

    suspend fun getUserByUsername(username: String): Users = withContext(Dispatchers.IO) {
        var user = Users()

        try {
            val snapshot = usersBranch.child(username).get().await()
            if (snapshot.exists()) {
                val profile = snapshot.child("profile").getValue(Int::class.java) ?: 0
                val firstname = snapshot.child("firstname").getValue(String::class.java) ?: ""
                val lastname = snapshot.child("lastname").getValue(String::class.java) ?: ""
                val bio = snapshot.child("bio").getValue(String::class.java) ?: ""
                val password = snapshot.child("password").getValue(String::class.java) ?: ""
                val profileURL = snapshot.child("imageURL").getValue(String::class.java)

                user = Users(username, firstname, lastname, bio, profile, password, profileURL)
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error fetching user by username", e)
        }

        return@withContext user
    }

    suspend fun getUsersByUsernames(usernames: Set<String>?): List<Users> = withContext(Dispatchers.IO) {
        val usersList = mutableListOf<Users>()

        try {
            usernames?.forEach { username ->
                val userSnapshot = usersBranch.child(username).get().await()
                if (userSnapshot.exists()) {
                    val profile = userSnapshot.child("profile").getValue(Int::class.java) ?: 0
                    val firstname = userSnapshot.child("firstname").getValue(String::class.java) ?: ""
                    val lastname = userSnapshot.child("lastname").getValue(String::class.java) ?: ""
                    val bio = userSnapshot.child("bio").getValue(String::class.java) ?: ""
                    val password = userSnapshot.child("password").getValue(String::class.java) ?: ""
                    val profileURL = userSnapshot.child("imageURL").getValue(String::class.java)

                    val user = Users(username, firstname, lastname, bio, profile, password, profileURL)
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

    suspend fun updateUserImage(username: String, newImageId: String): Boolean = withContext(Dispatchers.IO) {
        val userSnapshot = usersBranch.child(username)

        try {
            userSnapshot.child("imageURL").setValue(newImageId).await()
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error updating playlist image", e)
            return@withContext false
        }
    }

    suspend fun updateUserBio(username: String, newBio: String): Boolean = withContext(Dispatchers.IO) {
        val userSnapshot = usersBranch.child(username)

        try {
            userSnapshot.child("bio").setValue(newBio).await()
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error updating user bio", e)
            return@withContext false
        }
    }

    suspend fun updateFirstName(username: String, newFirstName: String): Boolean = withContext(Dispatchers.IO) {
        val userSnapshot = usersBranch.child(username)

        try {
            userSnapshot.child("firstname").setValue(newFirstName).await()
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error updating user bio", e)
            return@withContext false
        }
    }

    suspend fun updateLastName(username: String, newLastName: String): Boolean = withContext(Dispatchers.IO) {
        val userSnapshot = usersBranch.child(username)

        try {
            userSnapshot.child("lastname").setValue(newLastName).await()
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error updating user bio", e)
            return@withContext false
        }
    }
    suspend fun updatePassword(username: String, newPassword: String): Boolean = withContext(Dispatchers.IO) {
        val userSnapshot = usersBranch.child(username)
        Log.d("PASS", "$newPassword")
        try {
            userSnapshot.child("password").ref.setValue(newPassword).await()
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error updating user password", e)
            return@withContext false
        }
    }

    suspend fun isOldPasswordCorrect(username: String, oldPassword: String): Boolean = withContext(Dispatchers.IO) {
        val userSnapshot = usersBranch.child(username).get().await()

        try {
            val currentPassword = userSnapshot.child("password").getValue(String::class.java)
            return@withContext currentPassword == oldPassword
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error checking old password for username: $username", e)
            return@withContext false
        }
    }
}