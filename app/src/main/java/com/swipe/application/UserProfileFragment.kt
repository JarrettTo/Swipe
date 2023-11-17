package com.swipe.application

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class UserProfileFragment : Fragment() {
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private var selectedImageUri: Uri? = null
    private var originalDrawable: Drawable? = null

    private lateinit var firstNameOriginal: String
    private lateinit var lastNameOriginal: String
    private lateinit var bioOriginal: String
    private lateinit var uploadPhotoButton: Button
    private lateinit var firstNameText: EditText
    private lateinit var lastNameText: EditText
    private lateinit var bioText: EditText
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.user, container, false)

        val userPhoto: ImageView = view.findViewById(R.id.icon)
        uploadPhotoButton = view.findViewById(R.id.upload_photo_btn)
        firstNameText = view.findViewById(R.id.first_name_text)
        lastNameText = view.findViewById(R.id.last_name_text)
        bioText = view.findViewById(R.id.bio_text)
        oldPassword = view.findViewById(R.id.old_password_change)
        newPassword = view.findViewById(R.id.new_password_change)
        confirmPassword = view.findViewById(R.id.confirm_password_change)
        val confirmChangeBtn: Button = view.findViewById(R.id.confirm_changes_btn)
        val logoutBtn: Button = view.findViewById(R.id.logout_btn)

        originalDrawable = userPhoto.drawable
        firstNameOriginal = firstNameText.text.toString().trim()
        lastNameOriginal = lastNameText.text.toString().trim()
        bioOriginal = bioText.text.toString().trim()

        uploadPhotoButton.setOnClickListener {
            if (uploadPhotoButton.text == "Cancel") {
                revertToOriginalPhoto(userPhoto, uploadPhotoButton)
            } else {
                openGalleryForImage()
            }
        }

        confirmChangeBtn.setOnClickListener {
            if (hasUnsavedChanges()) {
                var count = 0
                if (oldPassword.text.isNotEmpty()) {
                    count += 1
                }
                if (newPassword.text.isNotEmpty()) {
                    count += 1
                }
                if (confirmPassword.text.isNotEmpty()) {
                    count += 1
                }
                if (count != 0 && count < 3) {
                    getActivity()?.let {
                        Toast.makeText(it, "Please fill up old, new, and confirm password fields", Toast.LENGTH_LONG).show()
                    }
                } else if (newPassword.text.toString() != confirmPassword.text.toString()) {
                    getActivity()?.let {
                        Toast.makeText(it, "New and confirm password do not match", Toast.LENGTH_LONG).show()
                    }
                }

                // DB CHANGE

            } else {
                getActivity()?.let {
                    Toast.makeText(it, "There are no unsaved changes", Toast.LENGTH_LONG).show()
                }
            }
        }

        logoutBtn.setOnClickListener {
            logoutUser()
        }

        return view
    }

    private fun logoutUser() {
        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        sharedPref?.edit()?.remove("userToken")?.apply()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            val userPhoto: ImageView = view?.findViewById(R.id.icon) ?: return
            userPhoto.setImageURI(selectedImageUri)
            uploadPhotoButton.text = "Cancel"
            uploadPhotoButton.setBackgroundColor(Color.RED)
        }
    }

    private fun revertToOriginalPhoto(userPhoto: ImageView, uploadPhotoButton: Button) {
        userPhoto.setImageDrawable(originalDrawable)
        uploadPhotoButton.text = "Upload Photo"
        uploadPhotoButton.setBackgroundColor(Color.parseColor("#F17300"))
        selectedImageUri = null
    }

    private fun hasUnsavedChanges(): Boolean {
        val isPhotoChanged = uploadPhotoButton.text == "Cancel"
        val isFirstNameChanged = firstNameText.text.toString() != firstNameOriginal
        val isLastNameChanged = lastNameText.text.toString() != lastNameOriginal
        val isBioChanged = bioText.text.toString() != bioOriginal
        val isPasswordNotEmpty = oldPassword.text.isNotEmpty() || newPassword.text.isNotEmpty() || confirmPassword.text.isNotEmpty()

        return isPhotoChanged || isFirstNameChanged || isLastNameChanged || isBioChanged || isPasswordNotEmpty
    }
}
