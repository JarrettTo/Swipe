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
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginBtn: Button
    private lateinit var signUpTextView: TextView
    private lateinit var user: Users
    private lateinit var userSession: UserSession
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userDataHelper: UserDataHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        userDataHelper = UserDataHelper()

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

//                userDataHelper.authenticateUser(username, password,
//                    onSuccess = {
//                        performLogin()
//                        goToMainActivity()
//                    },
//                    onFailure = { message ->
//                        showCustomToast("$message")
//                    }
//                )
                
                lifecycleScope.launch{
                    val passMatch = userDataHelper.isOldPasswordCorrect(username, password)

                    if(passMatch){
                        //val userClass = userDataHelper.getUserByUsername(username)
                        storeUserName(username)


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

    private fun storeUserName(username: String){
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()){
            putString("userName", username)
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

    inline fun SharedPreferences.getString(key: String, defaultValue: String): String {
        return getString(key, defaultValue) ?: defaultValue
    }

    inline fun SharedPreferences.Editor.putString(key: String, value: String) {
        putString(key, value)
    }

    fun SharedPreferences.getUser(): Users {
        return Users(
            username = getString("username", "") ?: "",
            password = getString("password", "") ?: ""
        )
    }

    fun SharedPreferences.Editor.putUser(user: Users) {
        putString("userName", user.username)
        putString("password", user.password)


    }
}