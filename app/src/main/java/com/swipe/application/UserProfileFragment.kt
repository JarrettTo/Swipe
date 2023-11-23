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
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.UUID

class UserProfileFragment : Fragment() {
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private var selectedImageUri: Uri? = null
    private var originalDrawable: Drawable? = null

    private lateinit var userSession: UserSession
    private lateinit var user: Users

    private lateinit var firstNameOriginal: String
    private lateinit var lastNameOriginal: String
    private lateinit var bioOriginal: String

    private lateinit var confirmPassword: EditText
    private lateinit var uploadPhotoButton: Button
    private lateinit var firstNameText: EditText
    private lateinit var lastNameText: EditText
    private lateinit var bioText: EditText
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var usernameText: TextView
    private lateinit var userPhoto: ImageView
    private lateinit var db : DatabaseHelper
    private var isPhotoChanged: Boolean = false
    private var isFirstNameChanged: Boolean = false
    private var isLastNameChanged: Boolean = false
    private var isBioChanged: Boolean = false
    private var isPasswordNotEmpty: Boolean = false

    private var userDataHelper = UserDataHelper()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.user, container, false)
        userSession = UserSession(requireContext())

        usernameText = view.findViewById(R.id.username_text)
        userPhoto = view.findViewById(R.id.icon)
        uploadPhotoButton = view.findViewById(R.id.upload_photo_btn)
        firstNameText = view.findViewById(R.id.first_name_text)
        lastNameText = view.findViewById(R.id.last_name_text)
        bioText = view.findViewById(R.id.bio_text)
        db = DatabaseHelper(requireContext())
        val confirmChangeBtn: Button = view.findViewById(R.id.confirm_changes_btn)
        val cancelChangeBtn: Button = view.findViewById(R.id.cancelButton)
        val logoutBtn: Button = view.findViewById(R.id.logout_btn)

        uploadPhotoButton.setOnClickListener {
            val isCancelMode = uploadPhotoButton.text.toString() == "Cancel"

            uploadPhotoButton.isSelected = !isCancelMode
            if (uploadPhotoButton.text == "Cancel") {
                revertToOriginalPhoto()
            } else {
                openGalleryForImage()
            }
        }

        confirmChangeBtn.setOnClickListener {
            if (hasUnsavedChanges()) {
                if (isPasswordNotEmpty) updatePassword()
                if (isPhotoChanged) updatePhoto()
                if (isBioChanged) updateBio()
                if (isFirstNameChanged) updateFirstName()
                if (isLastNameChanged) updateLastName()

            } else {
                getActivity()?.let {
                    showCustomToast("There are no unsaved changes")
                }
            }

            firstNameText.clearFocus()
            lastNameText.clearFocus()
            bioText.clearFocus()
            oldPassword.clearFocus()
            newPassword.clearFocus()
            confirmPassword.clearFocus()
        }

        cancelChangeBtn.setOnClickListener {
            revertBackChanges()
        }

        logoutBtn.setOnClickListener {
            logoutUser()
        }

        uploadPhotoButton.setOnClickListener {
            if (uploadPhotoButton.text == "Upload Photo") {
                openGalleryForImage()
            } else {
                uploadPhotoButton.text = "Upload Photo"
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        oldPassword = view.findViewById(R.id.old_password_change)
        newPassword = view.findViewById(R.id.new_password_change)
        confirmPassword = view.findViewById(R.id.confirm_password_change)

        userSession = UserSession(requireContext())
        userDataHelper = UserDataHelper()

        lifecycleScope.launch {
            user = userSession.userName?.let { userDataHelper.getUserByUsername(it) }!!

            usernameText.text = user.username
            firstNameText.setText(user.firstname)
            lastNameText.setText(user.lastname)
            bioText.setText(user.bio)

            Log.d("PROFILEURL", "${user.profileURL}")
            if (user.profileURL != "") {
                Glide.with(this@UserProfileFragment)
                    .load(user.profileURL)
                    .placeholder(R.drawable.dp)
                    .error(R.drawable.dp)
                    .into(userPhoto)
            } else {
                userPhoto.setImageResource(R.drawable.dp)
            }

            originalDrawable = userPhoto.drawable
            firstNameOriginal = firstNameText.text.toString().trim()
            lastNameOriginal = lastNameText.text.toString().trim()
            bioOriginal = bioText.text.toString().trim()
        }
    }

    private fun logoutUser() {
        db.clearDatabase()
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
        }
    }

    private fun revertBackChanges() {
        revertToOriginalPhoto()
        bioText.setText(bioOriginal)
        firstNameText.setText(firstNameOriginal)
        lastNameText.setText(firstNameOriginal)
        oldPassword.setText("")
        newPassword.setText("")
        confirmPassword.setText("")

        firstNameText.clearFocus()
        lastNameText.clearFocus()
        bioText.clearFocus()
        oldPassword.clearFocus()
        newPassword.clearFocus()
        confirmPassword.clearFocus()
    }

    private fun revertToOriginalPhoto() {
        userPhoto.setImageDrawable(originalDrawable)
        uploadPhotoButton.text = "Upload Photo"
        selectedImageUri = null
        isPhotoChanged = false
    }

    private fun hasUnsavedChanges(): Boolean {
        isPhotoChanged = uploadPhotoButton.text == "Cancel"
        isFirstNameChanged = firstNameText.text.toString().trim() != firstNameOriginal
        isLastNameChanged = lastNameText.text.toString().trim() != lastNameOriginal
        isBioChanged = bioText.text.toString().trim() != bioOriginal
        isPasswordNotEmpty = oldPassword.text.isNotEmpty() || newPassword.text.isNotEmpty() || confirmPassword.text.isNotEmpty()

        return isPhotoChanged || isFirstNameChanged || isLastNameChanged || isBioChanged || isPasswordNotEmpty
    }

    private fun updatePassword() {
        var count = 0

        if (oldPassword.text.isNotEmpty()) count += 1
        if (newPassword.text.isNotEmpty()) count += 1
        if (confirmPassword.text.isNotEmpty()) count += 1

        if (count != 0 && count < 3) {
            showCustomToast("Failed to update password. Please fill up old, new, and confirm password fields")
        } else if (newPassword.text.toString() != confirmPassword.text.toString()) {
            showCustomToast("Failed to update password. New and confirm password do not match")
        } else {
            lifecycleScope.launch {
                val checkOldPass = userDataHelper.isOldPasswordCorrect(user.username, oldPassword.text.toString())
                if (checkOldPass) {
                    val updateResult = userDataHelper.updatePassword(user.username, confirmPassword.text.toString())
                    if (updateResult) {
                        showCustomToast("Password updated successfully")
                    } else {
                        showCustomToast("Failed to update password. Please try again later.")
                    }
                } else {
                    showCustomToast("Incorrect old password")
                }

                oldPassword.setText("")
                newPassword.setText("")
                confirmPassword.setText("")
            }
        }

        isPasswordNotEmpty = false
    }


    private fun updatePhoto() {
        val storageReference = FirebaseStorage.getInstance().getReference()
        val imageRef = storageReference.child("images/users/${user.username}/${UUID.randomUUID()}")

        selectedImageUri?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        lifecycleScope.launch {
                            val imageUrl = downloadUri.toString().trim()
                            userDataHelper.updateUserImage(user.username, imageUrl)
                            Glide.with(this@UserProfileFragment).load(imageUrl).into(userPhoto)

                            uploadPhotoButton.text = "Upload Photo"
                            isPhotoChanged = false

                            showCustomToast("Photo updated successfully")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Log or show the error message
                    Log.e("UserProfileFragment", "Error uploading image: ${exception.message}")
                    showCustomToast("Failed to upload photo.")
                }
        } ?: run {
            showCustomToast("No image selected.")
        }
    }


    private fun updateBio() {
        lifecycleScope.launch {
            userDataHelper.updateUserBio(user.username, bioText.text.toString().trim())
            bioOriginal = bioText.text.toString().trim()
            isBioChanged = false

            showCustomToast("Bio updated successfully.")
        }
    }

    private fun updateFirstName() {
        lifecycleScope.launch {
            userDataHelper.updateFirstName(user.username, firstNameText.text.toString().trim())
            firstNameOriginal = firstNameText.text.toString().trim()
            isFirstNameChanged = false

            showCustomToast("First Name updated successfully.")
        }
    }

    private fun updateLastName() {
        lifecycleScope.launch {
            userDataHelper.updateLastName(user.username, lastNameText.text.toString().trim())
            lastNameOriginal = lastNameText.text.toString().trim()
            isLastNameChanged = false

            showCustomToast("Last Name updated successfully.")
        }
    }

    private fun showCustomToast(message: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, requireActivity().findViewById(R.id.toast_container))

        val textView: TextView = layout.findViewById(R.id.toast_text)
        textView.text = message

        with (Toast(requireContext())) {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

}
