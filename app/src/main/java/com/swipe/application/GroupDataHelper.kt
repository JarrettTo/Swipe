package com.swipe.application

import android.net.Uri
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CompletableDeferred

class GroupDataHelper {

    suspend fun retrieveGroups(groupsId: MutableSet<String>?): ArrayList<Groups> = withContext(Dispatchers.IO){
        val groups = ArrayList<Groups>()
        val countDownLatch = CountDownLatch(groupsId!!.size)
        for (id in groupsId) {
            val groupRef = FirebaseDatabase.getInstance().getReference("test").child("groups").child(id)
            try {
                Log.d("TEST:", "CHECK ")
                val snapshot = groupRef.get().await()
                if (snapshot.exists()) {
                    val groupId = snapshot.child("id").getValue(String::class.java) ?: ""
                    val groupName = snapshot.child("name").getValue(String::class.java) ?: ""
                    val groupCount = snapshot.child("count").getValue(Int::class.java) ?: 0
                    val groupDesc = snapshot.child("desc").getValue(String::class.java) ?: ""
                    val groupImage = snapshot.child("uri").getValue(String::class.java) ?: ""
                    val groupLikedGames = snapshot.child("likes").getValue<ArrayList<String>>() ?: arrayListOf()

                    val group = Groups(groupId, groupName, groupCount,groupDesc, groupImage, groupLikedGames)
                    Log.d("WHYY:", "CHECK ${group}")
                    groups.add(group)


                }
            } catch (e: Exception) {
                // Handle exceptions
                Log.e("FirebaseError", "Error fetching data", e)
            }
        }
        return@withContext groups
    }

    suspend fun retrieveGroup(id: String): Groups? = withContext(Dispatchers.IO){
        val groupRef =   FirebaseDatabase.getInstance().getReference("test").child("groups").child(id)
        try {
            Log.d("TEST:", "CHECK ")
            val snapshot = groupRef.get().await()
            if (snapshot.exists()) {
                val groupId = snapshot.child("id").getValue(String::class.java) ?: ""
                val groupName = snapshot.child("name").getValue(String::class.java) ?: ""
                val groupCount = snapshot.child("count").getValue(Int::class.java) ?: 0
                val groupDesc = snapshot.child("desc").getValue(String::class.java) ?: ""
                val groupImage = snapshot.child("uri").getValue(String::class.java) ?: ""
                val groupLikedGames = snapshot.child("likes").getValue<ArrayList<String>>() ?: arrayListOf()

                val group = Groups(groupId, groupName, groupCount,groupDesc, groupImage, groupLikedGames)
                return@withContext group
            }
        } catch (e: Exception) {
            // Handle exceptions
            Log.e("FirebaseError", "Error fetching data", e)
        }

        return@withContext null
    }

    suspend fun insertGroup(name: String, desc: String, uri: Uri, user: String): Groups? = withContext(Dispatchers.IO){
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupRef = dbRef.child("groups")
        val userRef = dbRef.child("users")
        val newUser= groupRef.push()
        lateinit var image : String
        try {
            Log.d("TEST:", "CHECK ")
            newUser.child("name").setValue(name)
            newUser.child("desc").setValue(desc)
            image = uploadImageToFirebase(uri, newUser.key.toString() ).toString()
            newUser.child("uri").setValue(image)
            newUser.child("count").setValue(1)
            newUser.child("likes")
            userRef.child(user).child("groups").orderByKey().limitToLast(1).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        // Get the last key and increment it
                        val lastKey = snapshot.children.last().key?.toIntOrNull() ?: 0
                        val newKey = lastKey + 1

                        // Use newKey to write the new group data
                        userRef.child(user).child("groups").child(newKey.toString()).setValue(newUser.key.toString())
                    } else {
                        // If there are no children, start with key 1
                        userRef.child(user).child("groups").child("1").setValue(newUser.key.toString())
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
            return@withContext Groups(newUser.key.toString(),name,1,desc,image, arrayListOf())
        } catch (e: Exception) {
            // Handle exceptions
            Log.e("FirebaseError", "Error fetching data", e)
        }

        return@withContext null
    }

    suspend fun joinGroup(code: String, user: String): Boolean= withContext(Dispatchers.IO){
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val userRef = dbRef.child("users")
        val groupRef = dbRef.child("groups").child(code).child("count")
        try {
            groupRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val currentCount = mutableData.getValue(Int::class.java) ?: 0
                    mutableData.value = currentCount + 1
                    return Transaction.success(mutableData)
                }

                override fun onComplete(databaseError: DatabaseError?, b: Boolean, dataSnapshot: DataSnapshot?) {
                    // This method will be called once the transaction is complete
                    if (databaseError != null) {
                        Log.e("FirebaseError", "Transaction failed: ", databaseError.toException())
                    }
                }
            })
            userRef.child(user).child("groups").orderByKey().limitToLast(1).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        // Get the last key and increment it
                        val lastKey = snapshot.children.last().key?.toIntOrNull() ?: 0
                        val newKey = lastKey + 1

                        // Use newKey to write the new group data
                        userRef.child(user).child("groups").child(newKey.toString()).setValue(code)
                    } else {
                        // If there are no children, start with key 1
                        userRef.child(user).child("groups").child("1").setValue(code)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error

                }
            })
            return@withContext true
        } catch (e: Exception) {
            // Handle exceptions
            Log.e("FirebaseError", "Error fetching data", e)
        }

        return@withContext false
    }

    suspend fun retrieveUserGroups(userName: String?) : MutableSet<String>? = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val userRef = dbRef.child("users").child(userName!!).child("groups")
        val groups = mutableSetOf<String>()
        try {
            Log.d("TEST:", "CHECK ")
            val snapshot = userRef.get().await()
            if (snapshot.exists()) {
                for (groupSnapshot in snapshot.children) {
                    groupSnapshot.getValue(String::class.java)?.let { groupId ->
                        groups.add(groupId)
                    }
                }
            }
            return@withContext groups
        } catch (e: Exception) {
            // Handle exceptions
            Log.e("FirebaseError", "Error fetching data", e)
        }
        return@withContext null





    }

    suspend fun uploadImageToFirebase(fileUri: Uri, groupId:String) : Uri? = withContext(Dispatchers.IO) {
        val storageRef = FirebaseStorage.getInstance().reference
        // Create a reference to 'images/mountains.jpg'
        val imageRef = storageRef.child("images/groups/${groupId}")
        val deferredUri = CompletableDeferred<Uri?>()
        val uploadTask = imageRef.putFile(fileUri)
        uploadTask.addOnSuccessListener {
            // Get a URL to the uploaded content
            imageRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.d("Upload", "Image uploaded: $downloadUri")
                    deferredUri.complete(downloadUri)
                    // You can store 'downloadUri.toString()' to Firebase Database if you want
                }
                else{
                    deferredUri.complete(null)
                }
            }
        }.addOnFailureListener {
            // Handle unsuccessful uploads
            deferredUri.completeExceptionally(it)
            Log.e("Upload", "Uploading image failed", it)
        }
        return@withContext deferredUri.await()
    }
}