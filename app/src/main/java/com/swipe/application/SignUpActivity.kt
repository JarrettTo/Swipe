package com.swipe.application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        val usernameEditText: EditText = findViewById(R.id.username_signup)
        val passwordEditText: EditText = findViewById(R.id.password_signup)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirm_password_signup)
        val signUpButton: Button = findViewById(R.id.signupBtn)
        val loginTextView: TextView = findViewById(R.id.loginText)

        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirm = confirmPasswordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()){
                Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_LONG).show()
            } else {
               if (password != confirm) {
                   Toast.makeText(this, "Password does not match", Toast.LENGTH_LONG).show()
               } else {
                   // DB CHECKER
                   val intent = Intent(this, LoginActivity::class.java)
                   startActivity(intent)
                   finish()
               }
            }
        }

        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
