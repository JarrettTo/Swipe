package com.swipe.application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import org.w3c.dom.Text

class Login: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

//        var username: TextView = findViewById(R.id.username_log)
//        var password: TextView = findViewById(R.id.password_log)
        val loginBtn: Button = findViewById(R.id.LoginBtn)
        loginBtn.setOnClickListener {

            // Navigate to the MainActivity / Homepage
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}