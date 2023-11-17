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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val usernameEditText: EditText = findViewById(R.id.username_log)
        val passwordEditText: EditText = findViewById(R.id.password_log)
        val loginBtn: Button = findViewById(R.id.LoginBtn)
        val signUpTextView: TextView = findViewById(R.id.signUpText)

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
                performLogin()
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
}
