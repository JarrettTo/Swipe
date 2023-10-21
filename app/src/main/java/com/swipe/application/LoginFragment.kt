package com.swipe.application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class LoginFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login, container, false)

//        var username: TextView = findViewById(R.id.username_log)
//        var password: TextView = findViewById(R.id.password_log)
        val loginBtn: Button = view.findViewById(R.id.LoginBtn)
        loginBtn.setOnClickListener {

            // Navigate to the MainActivity / Homepage
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}