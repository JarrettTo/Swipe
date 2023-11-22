package com.swipe.application

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.tasks.await

class SignUpActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userSession: UserSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()

        val usersBranch: DatabaseReference = database.getReference("test")
        val userIn = usersBranch.child("users")

        usernameEditText = findViewById(R.id.username_signup)
        passwordEditText = findViewById(R.id.password_signup)
        confirmPasswordEditText = findViewById(R.id.confirm_password_signup)
        signUpButton = findViewById(R.id.signupBtn)
        loginTextView = findViewById(R.id.loginText)

        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirm = confirmPasswordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_LONG)
                    .show()
            } else {
                if (password != confirm) {
                    Toast.makeText(this, "Password does not match", Toast.LENGTH_LONG).show()
                } else {
                    //Compares existing usernames with inputted username
                    userIn.equalTo(username)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (userSnapshot in dataSnapshot.children) {
                                        val user = userSnapshot.getValue(Users::class.java)
                                        if (user != null && user.password == password) {
                                            // Username and password match
                                            Toast.makeText(this@SignUpActivity, "This username already exists", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    // Username does not exist: make new entry
                                    userIn.child(username).child("password").setValue(password)
                                    performLogin()
                                    goToMainActivity()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle errors, if any
                            }
                        })

                }

            }
        }


        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun performLogin() {
        storeUserSession("user_token_or_identifier")
    }

    private fun storeUserSession(token: String) {
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("userToken", token)
            apply()
        }

    }

    private fun goToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
