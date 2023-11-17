package com.swipe.application

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class UserProfileFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user, container, false)

        val userPhoto: ImageView = view.findViewById(R.id.icon)
        val usernameText: TextView = view.findViewById(R.id.username_text)
        val firstNameText: EditText = view.findViewById(R.id.first_name_text)
        val lastNameText: EditText = view.findViewById(R.id.last_name_text)
        val oldPassword: EditText = view.findViewById(R.id.old_password_change)
        val newPassword: EditText = view.findViewById(R.id.new_password_change)
        val confirmPasswordChangeBtn: Button = view.findViewById(R.id.confirm_password_change_btn)
        val logoutBtn: Button = view.findViewById(R.id.logout_btn)

        confirmPasswordChangeBtn.setOnClickListener {
        }

        logoutBtn.setOnClickListener {
            logoutUser()
        }


        return view
    }

    private fun logoutUser() {
        // Clear the stored session token
        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        sharedPref?.edit()?.remove("userToken")?.apply()

        // Redirect to the LoginActivity
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish() // Close the current activity (or container activity)
    }
}
