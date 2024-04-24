package com.medicalblvd.dermacheck.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

import com.medicalblvd.dermacheck.data.UserInfo
import java.util.UUID

class UserRepository @Inject constructor (
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
){

    private val usersCollection: String = "users"

    fun checkIfUsernameExists(
        username: String,
        callback: (Boolean) -> Unit) {
        database.collection(usersCollection)
            .whereEqualTo("username", username).get()
            .addOnSuccessListener { documents ->
                callback(documents.size() > 0)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun login(
        email: String,
        password: String,
        onLoginComplete: (Boolean, Exception?) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            onLoginComplete(false, IllegalArgumentException("Please fill in all fields"))
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.currentUser?.uid?.let { uid ->
                        getUserData(uid) { userInfo, exception ->
                            if (userInfo != null) {
                                onLoginComplete(true, null)
                            } else {
                                onLoginComplete(false, exception ?: Exception("Failed to fetch user data"))
                            }
                        }
                    } ?: onLoginComplete(false, Exception("UID not found"))
                } else {
                    onLoginComplete(false, task.exception)
                }
            }
            .addOnFailureListener { exception ->
                onLoginComplete(false, exception)
            }
    }

    fun createUser(
        email: String,
        password: String,
        username: String,
        callback: (Boolean, Exception?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)

                //Create profile
                createOrUpdateProfile(name = "", username=username
                ) { b, exception ->
                    callback(true, null)
                }
                } else {
                    callback(false, task.exception)
                }
            }
    }

    fun createOrUpdateProfile(
        name: String? = null,
        username: String? = null,
        bio: String? = null,
        imageUrl: String? = null,
        onComplete: (Boolean, Exception?) -> Unit = { _, _ -> }
    ) {
        val uid = firebaseAuth.currentUser?.uid ?: run {
            onComplete(false, Exception("Not logged in"))
            return
        }

        // Fetch current user data first to compare against provided values
        fetchCurrentUserData(uid) { existingUserData ->
            val userData = prepareUserData(name, username, bio, imageUrl, existingUserData)
            if (existingUserData != null) {
                updateProfile(uid, userData, onComplete)
            } else {
                createProfile(uid, userData, onComplete)
            }
        }
    }

    private fun fetchCurrentUserData(uid: String, callback: (UserInfo?) -> Unit) {
        database.collection(usersCollection).document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    callback(document.toObject(UserInfo::class.java))
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    private fun prepareUserData(
        name: String?,
        username: String?,
        bio: String?,
        imageUrl: String?,
        existingUserData: UserInfo?
    ): UserInfo {
        return UserInfo(
            userId = firebaseAuth.currentUser?.uid,
            name = name ?: existingUserData?.name, // Use existing name if new name is not provided
            username = username ?: existingUserData?.username, // Use existing username if new username is not provided
            bio = bio ?: existingUserData?.bio, // Use existing bio if new bio is not provided
            imageUrl = imageUrl ?: existingUserData?.imageUrl, // Use existing imageUrl if new imageUrl is not provided
            following = existingUserData?.following // Retain existing following list
        )
    }

    private fun updateProfile(
        uid: String,
        userInfo: UserInfo,
        onComplete: (Boolean, Exception?) -> Unit) {
        database.collection(usersCollection)
            .document(uid).update(userInfo.toMap())
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    private fun createProfile(
        uid: String,
        userInfo: UserInfo,
        onComplete: (Boolean, Exception?) -> Unit) {
        database.collection(usersCollection).document(uid).set(userInfo.toMap())
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e)
            }
    }

    fun getUserData(uid: String, onDataReceived: (UserInfo?, Exception?) -> Unit) {
        database.collection(usersCollection).document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<UserInfo>()
                onDataReceived(user, null) // Pass the user data back to the ViewModel
            }
            .addOnFailureListener { exception ->
                onDataReceived(null, exception) // Pass the exception back to the ViewModel
            }
    }

    fun uploadImage(
        uri: Uri,
        onProgress: (Boolean) -> Unit,
        onSuccess: (Uri) -> Unit,
        onFailure: (Exception) -> Unit) {
        onProgress(true)

        val storageRef = firebaseStorage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                    onProgress(false)
                    onSuccess(downloadUri)
                }
            }
            .addOnFailureListener { exc ->
                onProgress(false)
                onFailure(exc)
            }
    }

}