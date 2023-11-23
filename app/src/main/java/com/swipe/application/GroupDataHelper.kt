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
                    val groupName = snapshot.child("name").getValue(String::class.java) ?: ""
                    val groupCreator = snapshot.child("creator").getValue(String::class.java) ?: ""
                    val groupCount = snapshot.child("count").getValue(Int::class.java) ?: 0
                    val groupDesc = snapshot.child("desc").getValue(String::class.java) ?: ""
                    val groupImage = snapshot.child("uri").getValue(String::class.java) ?: ""

                    val groupLikedGames = ArrayList<Games>()
                    val gamesSnapshot = snapshot.child("likes")
                    for (gameSnapshot in gamesSnapshot.children) {
                        val game = gameSnapshot.getValue(Games::class.java)
                        game?.let { groupLikedGames.add(it) }
                    }

                    val groupPlaylists = snapshot.child("playlists").getValue<ArrayList<String>>() ?: arrayListOf()
                    val groupUsers = snapshot.child("users").getValue<ArrayList<String>>() ?: arrayListOf()

                    val group = Groups(id, groupName, groupCreator, groupCount, groupDesc, groupImage, groupLikedGames, groupPlaylists, groupUsers)
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

    suspend fun retrieveGroupsName(groupsId: MutableSet<String>?): ArrayList<String> = withContext(Dispatchers.IO){
        val groups = arrayListOf("Personal Feed")
        val countDownLatch = CountDownLatch(groupsId!!.size)
        for (id in groupsId) {
            val groupRef = FirebaseDatabase.getInstance().getReference("test").child("groups").child(id)
            try {
                Log.d("TEST:", "CHECK ")
                val snapshot = groupRef.get().await()
                if (snapshot.exists()) {
                    val groupName = snapshot.child("name").getValue(String::class.java) ?: ""


                    groups.add(groupName)


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
                val groupName = snapshot.child("name").getValue(String::class.java) ?: ""
                val groupCreator = snapshot.child("creator").getValue(String::class.java) ?: ""
                val groupCount = snapshot.child("count").getValue(Int::class.java) ?: 0
                val groupDesc = snapshot.child("desc").getValue(String::class.java) ?: ""
                val groupImage = snapshot.child("uri").getValue(String::class.java) ?: ""

                val groupLikedGames = ArrayList<Games>()
                val gamesSnapshot = snapshot.child("likes")
                for (gameSnapshot in gamesSnapshot.children) {
                    val game = gameSnapshot.getValue(Games::class.java)
                    game?.let { groupLikedGames.add(it) }
                }

                val groupPlaylists = snapshot.child("playlists").getValue<ArrayList<String>>() ?: arrayListOf()
                val groupUsers = snapshot.child("users").getValue<ArrayList<String>>() ?: arrayListOf()

                val group = Groups(id, groupName, groupCreator, groupCount, groupDesc, groupImage, groupLikedGames, groupPlaylists, groupUsers)
                return@withContext group
            }
        } catch (e: Exception) {
            // Handle exceptions
            Log.e("FirebaseError", "Error fetching data", e)
        }

        return@withContext null
    }

    suspend fun insertGroup(name: String, desc: String, uri: Uri?, user: String): Groups? = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupRef = dbRef.child("groups")
        val userRef = dbRef.child("users")
        val newUserGroupsRef = userRef.child(user).child("groups")
        val newGroup = groupRef.push()
        lateinit var image: String

        try {
            // Upload image and set other group details
            image = uri?.let { uploadImageToFirebase(it, newGroup.key.toString()).toString() } ?: ""
            newGroup.child("name").setValue(name)
            newGroup.child("desc").setValue(desc)
            newGroup.child("creator").setValue(user)
            newGroup.child("uri").setValue(image)
            newGroup.child("count").setValue(1)
            newGroup.child("likes").setValue(ArrayList<String>())
            newGroup.child("playlists").setValue(ArrayList<String>())
            newGroup.child("users").setValue(arrayListOf(user))

            // Check if user already has groups, if not create the 'groups' node
            val userGroupsSnapshot = newUserGroupsRef.get().await()
            if (!userGroupsSnapshot.exists()) {
                newUserGroupsRef.child("1").setValue(newGroup.key.toString())
            } else {
                val newKey = (userGroupsSnapshot.children.mapNotNull { it.key?.toIntOrNull() }.maxOrNull() ?: 0) + 1
                newUserGroupsRef.child(newKey.toString()).setValue(newGroup.key.toString())
            }

            return@withContext Groups(newGroup.key.toString(), name, user, 1, desc, image, arrayListOf(), arrayListOf(), arrayListOf(user))
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error creating group", e)
            return@withContext null
        }
    }

    suspend fun removeGroup(groupID: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupRef = dbRef.child("groups").child(groupID)

        try {
            val snapshot = groupRef.get().await()
            val creatorUserId = snapshot.child("creator").getValue(String::class.java)

            if (creatorUserId != null) {
                groupRef.removeValue().await()

                val userGroupRef = dbRef.child("users").child(creatorUserId).child("groups")
                val userGroupsSnapshot = userGroupRef.get().await()
                var removedFromUser = false
                for (childSnapshot in userGroupsSnapshot.children) {
                    if (childSnapshot.value == groupID) {
                        val removeUserGroupTask = childSnapshot.ref.removeValue().await()
                        removedFromUser = true
                        break
                    }
                }

                return@withContext removedFromUser
            } else {
                Log.e("FirebaseError", "Creator user ID not found for group $groupID")
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error deleting group", e)
            return@withContext false
        }
    }

    suspend fun joinGroup(code: String, user: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupRef = dbRef.child("groups").child(code)
        try {
            val countRef = groupRef.child("count")
            countRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val currentCount = mutableData.getValue(Int::class.java) ?: 0
                    mutableData.value = currentCount + 1
                    return Transaction.success(mutableData)
                }

                override fun onComplete(databaseError: DatabaseError?, b: Boolean, dataSnapshot: DataSnapshot?) {
                    if (databaseError != null) {
                        Log.e("FirebaseError", "Transaction failed: ", databaseError.toException())
                    }
                }
            })

            val usersSnapshot = groupRef.child("users").get().await()
            val usersList = usersSnapshot.getValue<ArrayList<String>>() ?: arrayListOf()
            val userRef = dbRef.child("users")

            if (!usersList.contains(user)) {
                //groups
                usersList.add(user)
                groupRef.child("users").setValue(usersList).await()

                //users
                userRef.child(user).child("groups").orderByKey().limitToLast(1)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.hasChildren()) {
                                val lastKey = snapshot.children.last().key?.toIntOrNull() ?: 0
                                val newKey = lastKey + 1
                                userRef.child(user).child("groups").child(newKey.toString())
                                    .setValue(code)
                            } else {
                                userRef.child(user).child("groupd").child("1")
                                    .setValue(code)
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle error
                        }
                    })
            }

            return@withContext true
        } catch (e: Exception) {
            // Handle exceptions
            Log.e("FirebaseError", "Error in joining group", e)
            return@withContext false
        }
    }

    suspend fun leaveGroup(groupId: String, userName: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val userGroupRef = dbRef.child("users").child(userName).child("groups")
        val groupRef = dbRef.child("groups").child(groupId)

        try {
            val userGroupsSnapshot = userGroupRef.get().await()
            userGroupsSnapshot.children.forEach loop@{ childSnapshot ->
                if (childSnapshot.value == groupId) {
                    childSnapshot.ref.removeValue().await()
                    return@loop
                }
            }

            val countRef = groupRef.child("count")
            countRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val currentCount = mutableData.getValue(Int::class.java) ?: 0
                    if (currentCount > 0) {
                        mutableData.value = currentCount - 1
                    }
                    return Transaction.success(mutableData)
                }

                override fun onComplete(databaseError: DatabaseError?, b: Boolean, dataSnapshot: DataSnapshot?) {
                    if (databaseError != null) {
                        Log.e("FirebaseError", "Transaction failed: ", databaseError.toException())
                    }
                }
            })

            val groupUsersRef = groupRef.child("users")
            val groupUsersSnapshot = groupUsersRef.get().await()
            val groupUsersList = groupUsersSnapshot.getValue<ArrayList<String>>() ?: arrayListOf()
            groupUsersList.remove(userName)
            groupUsersRef.setValue(groupUsersList).await()

            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error in leaving group", e)
            return@withContext false
        }
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

    suspend fun updateGroupName(gameId: String, newGroupName: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupRef = dbRef.child("groups").child(gameId)

        try {
            groupRef.child("name").setValue(newGroupName).await()
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error updating group name", e)
            return@withContext false
        }
    }

    suspend fun updateGroupDesc(gameId: String, newGroupDesc: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupRef = dbRef.child("groups").child(gameId)

        try {
            groupRef.child("desc").setValue(newGroupDesc).await()
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error updating group description", e)
            return@withContext false
        }
    }

    suspend fun updateGroupImage(gameId: String, newImageId: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupRef = dbRef.child("groups").child(gameId)

        try {
            groupRef.child("uri").setValue(newImageId).await()
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error updating playlist image", e)
            return@withContext false
        }
    }

    suspend fun getCreator(groupID: String): String = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupRef = dbRef.child("groups").child(groupID)

        try {
            val snapshot = groupRef.get().await()
            val creatorUserId = snapshot.child("creator").getValue(String::class.java)

            return@withContext creatorUserId.toString()
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error deleting group", e)
            return@withContext ""
        }
    }

    suspend fun insertPlaylist(code: String, playlistId: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupRef = dbRef.child("groups").child(code)
        try {
            val playlistSnapshot = groupRef.child("playlists").get().await()
            val playlistList = playlistSnapshot.getValue<ArrayList<String>>() ?: arrayListOf()

            if (!playlistList.contains(playlistId)) {
                playlistList.add(playlistId)
                groupRef.child("playlists").setValue(playlistList).await()
            }

            return@withContext true
        } catch (e: Exception) {
            // Handle exceptions
            Log.e("FirebaseError", "Error in adding playlist to group", e)
            return@withContext false
        }
    }

    suspend fun removePlaylist(code: String, playlistId: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupRef = dbRef.child("groups").child(code)
        try {
            val playlistSnapshot = groupRef.child("playlists").get().await()
            val playlistList = playlistSnapshot.getValue<ArrayList<String>>() ?: arrayListOf()

            if (playlistList.contains(playlistId)) {
                playlistList.remove(playlistId)
                groupRef.child("playlists").setValue(playlistList).await()
                return@withContext true

            } else {
                return@withContext false
            }
        } catch (e: Exception) {
            // Handle exceptions
            Log.e("FirebaseError", "Error in removing playlist from group", e)
            return@withContext false
        }
    }

    suspend fun addGameToGroup(code: String, game: Games) = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupRef = dbRef.child("groups").child(code).child("likes")

        try {
            val gameRef = groupRef.push()
            gameRef.setValue(game)
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error adding game to group", e)
        }
    }

    suspend fun removeGameFromGroup(code: String, game: Games) = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupGamesRef = dbRef.child("groups").child(code).child("likes")

        try {
            groupGamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (gameSnapshot in snapshot.children) {
                            val getGame = gameSnapshot.getValue(Games::class.java)
                            if (getGame != null && getGame.gameId == game.gameId) {
                                gameSnapshot.ref.removeValue()
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("FirebaseError", "Error removing game from playlist", databaseError.toException())
                }
            })
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error removing game from playlist", e)
        }
    }

    suspend fun returnGames(groupId: String): ArrayList<Games> = withContext(Dispatchers.IO) {
        val gamesList = ArrayList<Games>()
        val groupRef = FirebaseDatabase.getInstance().getReference("test").child("groups").child(groupId)

        try {
            val snapshot = groupRef.child("likes").get().await()
            if (snapshot.exists()) {
                snapshot.children.forEach { childSnapshot ->
                    val game = childSnapshot.getValue(Games::class.java)
                    game?.let { gamesList.add(it) }
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error fetching liked games", e)
        }

        return@withContext gamesList
    }

    suspend fun isGameAlreadyInGroup(groupId: String, game: Games): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupGamesRef = dbRef.child("groups").child(groupId).child("likes")

        try {
            val snapshot = groupGamesRef.get().await()
            if (snapshot.exists()) {
                snapshot.children.forEach { gameSnapshot ->
                    val existingGame = gameSnapshot.getValue(Games::class.java)
                    if (existingGame != null && existingGame.gameId == game.gameId) {
                        return@withContext true
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error checking if game is in group", e)
        }
        return@withContext false
    }

    suspend fun isPlaylistAlreadyInGroup(groupId: String, playlist: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val groupPlaylistRef = dbRef.child("groups").child(groupId).child("playlists")

        try {
            val snapshot = groupPlaylistRef.get().await()
            if (snapshot.exists()) {
                for (playlistSnapshot in snapshot.children) {
                    val existingPlaylist = playlistSnapshot.getValue(String::class.java)
                    if (existingPlaylist != null && existingPlaylist == playlist) {
                        return@withContext true
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error checking if playlist is in group", e)
        }
        return@withContext false
    }

}