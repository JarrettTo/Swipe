package com.swipe.application

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginBtn: Button
    private lateinit var signUpTextView: TextView
    private lateinit var user: Users
    private lateinit var userSession: UserSession
    private lateinit var userDataHelper: UserDataHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        userDataHelper = UserDataHelper()

        usernameEditText = findViewById(R.id.username_log)
        passwordEditText = findViewById(R.id.password_log)
        loginBtn = findViewById(R.id.LoginBtn)
        signUpTextView = findViewById(R.id.signUpText)
        userSession = UserSession(this)

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
                lifecycleScope.launch{
                    val passMatch = userDataHelper.isOldPasswordCorrect(username, password)

                    if(passMatch){
                        user = userDataHelper.getUserByUsername(username)
                        userSession.user = user

                        storeUserSession(username)
                    }else{
                        showCustomToast("User not found")
                    }
                }
            } else {
                showCustomToast("Username and password cannot be empty")
            }
        }

        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isUserLoggedIn(): Boolean {

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        Log.d("AM I LOG", "${sharedPref.getString("userToken", null)}")
        return sharedPref.getString("userToken", null) != null
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