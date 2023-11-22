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

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginBtn: Button
    private lateinit var signUpTextView: TextView
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()

        val usersBranch: DatabaseReference = database.getReference("test")
        val userIn = usersBranch.child("users")
         usernameEditText = findViewById(R.id.username_log)
         passwordEditText = findViewById(R.id.password_log)
         loginBtn = findViewById(R.id.LoginBtn)
         signUpTextView = findViewById(R.id.signUpText)

        if (isUserLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        loginBtn.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            Log.d("LOL", "$username $password")
            if (username.isNotEmpty() && password.isNotEmpty()) {
                //DB CHECKER
//                performLogin()
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)

                userIn.equalTo(username)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (userSnapshot in dataSnapshot.children) {
                                    val user = userSnapshot.getValue(Users::class.java)
                                    if (user != null && user.password == password) {
                                        // Username and password match
                                        Log.d("LOL", "$user")
                                        performLogin()
                                        goToMainActivity()
                                    }
                                }
                            } else {
                                // Username does not exist
                                Toast.makeText(this@LoginActivity, "This user does not exist", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle errors, if any
                        }
                    })
            } else {
                Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_LONG).show()
            }
        }

        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("userToken", null) != null
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

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun goToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
