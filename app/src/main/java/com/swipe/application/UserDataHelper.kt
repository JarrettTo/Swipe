package com.swipe.application

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
}
