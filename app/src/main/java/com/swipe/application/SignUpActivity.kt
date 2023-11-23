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
import kotlinx.coroutines.tasks.await

class SignUpActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var userSession: UserSession
    private lateinit var userDataHelper: UserDataHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        userDataHelper = UserDataHelper()

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
                    showCustomToast("Username and password cannot be empty")
            } else {
                if (password != confirm) {
                    showCustomToast("Password does not match")
                } else {
                    userDataHelper.createUser(username, password,
                        onSuccess = {
                            performLogin()
                            storeUserName(username)
                            storePassword(password)
                            goToMainActivity()

                        },
                        onFailure = {
                            // Handle failure
                        },
                        onUserExists = {
                            Toast.makeText(this@SignUpActivity, "This username already exists", Toast.LENGTH_LONG).show()
                        }
                    )
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

    private fun storeUserName(username: String){
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()){
            putString("userName", username)
            apply()
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun storePassword(password: String){
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()){
            putString("password", password)
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

    private fun showCustomToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.toast_container))

        val textView: TextView = layout.findViewById(R.id.toast_text)
        textView.text = message

        with (Toast(this)) {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

}
