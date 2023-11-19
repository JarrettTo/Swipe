package com.swipe.application
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
class GroupFragment : Fragment() {
    private lateinit var groupView: RecyclerView
    private var selectedImageUri: Uri? = null
    private lateinit var userSession: UserSession
    private lateinit var groupList: ArrayList<Groups>
    private lateinit var adapter: GroupAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.groups, container, false)
        userSession = UserSession(requireContext())
        val groups = userSession.groups
        groupView = view.findViewById(R.id.recycleGroup)
        val layoutManager = LinearLayoutManager(requireContext())
        groupView.layoutManager = layoutManager
        Log.d("GROUPIDS:", "CHECK ${groups}")
        lifecycleScope.launch {
            val groupList = DataHelper.retrieveGroups(groups)
            Log.d("GROUP:", "CHECK ${groupList}")
            adapter = GroupAdapter(groupList)
            groupView.adapter = adapter
            // Now 'groupsList' contains your data
            // Update your UI here, e.g., set the data to an adapter
        }
        val newButton: Button = view.findViewById(R.id.button)
        val joinButton: Button = view.findViewById(R.id.button2)
        newButton.setOnClickListener {
            showAddGroupDialog()
        }
        joinButton.setOnClickListener{
            showJoinGroupDialog()
        }


        // Pass a listener to the adapter to handle item clicks



        return view
    }
    private fun showAddGroupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.create_group, null)
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
            createGroup(groupNameEditText.text.toString(), groupDescEditText.text.toString(), "WOW")
            alertDialog.dismiss()
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
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }
    private fun createGroup(name:String, desc:String, uri: String){

        lifecycleScope.launch {
            var groupCode = DataHelper.insertGroup(name, desc, uri, userSession.userName!!)
            Log.d("CODE", "CODE: ${groupCode}")
            userSession.addGroupId(groupCode)
            val newGroup = Groups(groupCode, name, 1, desc, uri,arrayListOf())
            adapter.addGroup(newGroup)
            adapter?.notifyDataSetChanged()
            // Now 'groupsList' contains your data
            // Update your UI here, e.g., set the data to an adapter
        }


    }
    interface JoinGroupCallback {
        fun onResult(success: Boolean)
    }
    private fun joinGroup(code:String, callback: JoinGroupCallback) {

        lifecycleScope.launch {

            val group = DataHelper.retrieveGroup(code)
            Log.d("group", "group: ${group}")
            if(group != null){
                if(!userSession.addGroupId(code)){
                    Toast.makeText(context, "You're already part of that group!", Toast.LENGTH_SHORT).show()
                    callback.onResult(false)
                }
                else{
                    DataHelper.joinGroup(code, userSession.userName!!)
                    adapter.addGroup(group)
                    adapter?.notifyDataSetChanged()
                    callback.onResult(true)
                }


            }else{
                Toast.makeText(context, "Invalid Group Code!", Toast.LENGTH_SHORT).show()
                callback.onResult(false)
            }

            // Now 'groupsList' contains your data
            // Update your UI here, e.g., set the data to an adapter
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            val userPhoto: ImageView = view?.findViewById(R.id.icon) ?: return
            userPhoto.setImageURI(selectedImageUri)

        }
    }

}
