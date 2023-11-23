package com.swipe.application

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.service.autofill.UserData
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.*

interface GroupDetailsListener {
    fun onGroupUpdated(groups: ArrayList<String>)
    fun onGroupDeleted(group: Groups)

    fun onGroupClicked(group: Groups)

}
class GroupFragment : Fragment() , GroupDetailsListener {
    private lateinit var groupView: RecyclerView
    private var selectedImageUri: Uri? = null
    private lateinit var userSession: UserSession
    private lateinit var groupList: ArrayList<Groups>
    private lateinit var adapter: GroupAdapter
    private val groupDataHelper = GroupDataHelper()
    private val userDataHelper = UserDataHelper()
    private lateinit var dialogView : View

    override fun onGroupUpdated(groups: ArrayList<String>) {
        lifecycleScope.launch {
            val updated = groupDataHelper.retrieveGroups(groups.toMutableSet())
            adapter.updateGroups(updated)
        }
    }

    override fun onGroupDeleted(group: Groups) {
        adapter.removeGroup(group)
    }

    override fun onGroupClicked(group: Groups) {
        val intent = Intent(requireContext(), GroupDetailsActivity::class.java).apply {
            putExtra("GroupDetails", group)
        }
        startActivityForResult(intent, 2)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.groups, container, false)

        userSession = UserSession(requireContext())

        groupView = view.findViewById(R.id.recycleGroup)
        groupView.layoutManager = LinearLayoutManager(requireContext())

        val newButton: Button = view.findViewById(R.id.button)
        val joinButton: Button = view.findViewById(R.id.button2)
        newButton.setOnClickListener {
            showAddGroupDialog()
        }
        joinButton.setOnClickListener {
            showJoinGroupDialog()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = GroupAdapter(arrayListOf(), this, lifecycleScope)
        groupView.adapter = adapter

        lifecycleScope.launch {
            val groups = userDataHelper.retrieveUserGroups(userSession.userName)
            val groupDetails = groupDataHelper.retrieveGroups(groups)
            adapter.updateGroups(groupDetails)
        }
    }

    private fun showAddGroupDialog() {
        dialogView = layoutInflater.inflate(R.layout.create_group, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)

        val alertDialog = dialogBuilder.create()

        val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)
        val createButton: Button = dialogView.findViewById(R.id.createButton)
        val groupNameEditText: EditText = dialogView.findViewById(R.id.groupName)
        val groupDescEditText: EditText = dialogView.findViewById(R.id.groupDesc)
        val groupBannerbtn: Button = dialogView.findViewById(R.id.uploadBtn)


        groupBannerbtn.setOnClickListener {
            val isCancelMode = groupBannerbtn.text.toString() == "Cancel"

            groupBannerbtn.isSelected = !isCancelMode
            if (groupBannerbtn.text != "Cancel") {
                openGalleryForImage()
            }
        }
        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        createButton.setOnClickListener {
            if (groupNameEditText.text.toString() != "") {
                val name = groupNameEditText.text.toString().trim()
                val desc = groupDescEditText.text.toString()
                val uri = selectedImageUri

                lifecycleScope.launch {
                    val groups = userDataHelper.retrieveUserGroups(userSession.userName)
                    val existingGroups = groupDataHelper.retrieveGroups(groups)
                    val groupIndex = existingGroups.indexOfFirst { it.name.trim() == name }
                    val isGroupExistingAndNotCreatedByUser = groupIndex != -1 && groupDataHelper.getCreator(existingGroups[groupIndex].id) != userSession.userName

                    Log.d(
                        "groups",
                        "Groups: $groups"
                    )
                    Log.d(
                        "existingGroups",
                        "Existing Groups: $existingGroups"
                    )
                    Log.d(
                        "groupIndex",
                        "Group Index: $groupIndex"
                    )
                    Log.d(
                        "isGroupExistingAndNotCreatedByUser",
                        "Is Group Existing and Not Created by User: $isGroupExistingAndNotCreatedByUser"
                    )

                    if (isGroupExistingAndNotCreatedByUser) {
                        val newGroup = groupDataHelper.insertGroup(name, desc, uri, userSession.userName!!)
                        Log.d("CODE", "CODE: ${newGroup?.id}")
                        Log.d("URI:", "$uri")

                        userSession.addGroupId(newGroup?.id!!)
                        adapter.addGroup(newGroup)

                        alertDialog.dismiss()
                    } else {
                        showCustomToast("You have already created a group with name $name")
                    }
                }
            } else {
                showCustomToast("Group Name should not be empty")
            }
        }

        alertDialog.show()
    }

    private fun showJoinGroupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.join_group, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)

        val alertDialog = dialogBuilder.create()

        val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)
        val createButton: Button = dialogView.findViewById(R.id.createButton)
        val groupCodeEditText: EditText = dialogView.findViewById(R.id.groupCode)

        createButton.text = "join"

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        createButton.setOnClickListener {
            joinGroup(groupCodeEditText.text.toString(), object : JoinGroupCallback {
                override fun onResult(success: Boolean) {
                    if (success) {
                        alertDialog.dismiss()
                    } else {
                        groupCodeEditText.setText("")
                    }
                }
            })

        }

        alertDialog.show()
    }

    interface JoinGroupCallback {
        fun onResult(success: Boolean)
    }
    private fun joinGroup(code:String, callback: JoinGroupCallback) {
        lifecycleScope.launch {
            val group = groupDataHelper.retrieveGroup(code)

            val UserGroupList = userDataHelper.retrieveUserGroups(userSession.userName)
            val isPartOfGroup = UserGroupList?.any { checkGroup -> checkGroup.trim() == code }

            if (group != null) {
                if (isPartOfGroup == true) {
                    showCustomToast("You're already part of that group!")
                    callback.onResult(false)
                } else {
                    groupDataHelper.joinGroup(code, userSession.userName!!)
                    group.count = group.count + 1
                    withContext(Dispatchers.Main) {
                        adapter.addGroup(group)
                        adapter.notifyDataSetChanged()
                    }
                    callback.onResult(true)
                }
            } else {
                showCustomToast("Invalid Group Code!")
                callback.onResult(false)
            }
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            Log.d("URI", "${selectedImageUri}")
            val userPhoto: ImageView? = dialogView.findViewById(R.id.bannerImage)
            Glide.with(this)
                .load(selectedImageUri)
                .into(userPhoto!!)
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val returnedGroup = data?.getSerializableExtra("returnedGroup") as? Groups
            if (returnedGroup != null) {
                adapter.updateGroup(returnedGroup)
            }
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
