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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

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
        userSession = UserSession(this)

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
                    val intent = Intent(this, LoginActivity::class.java)
                    lifecycleScope.launch{
                        if (userDataHelper.getUserByUsername(username) == null) {
                            userDataHelper.createUser(username, password)

                            startActivity(intent)
                            finish()
                        } else {
                            showCustomToast("User already exists. Try another one")
                        }
                    }
                }
            }
        }

        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
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
