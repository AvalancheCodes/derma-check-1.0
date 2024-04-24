package com.medicalblvd.dermacheck

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.medicalblvd.dermacheck.data.Event
import com.medicalblvd.dermacheck.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

import com.medicalblvd.dermacheck.data.UserInfo

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val userRepository: UserRepository
) : ViewModel() {
    val inProgress = MutableLiveData<Boolean>()

    private val errorMessage = MutableLiveData<String>()

    val signedIn = MutableLiveData<Boolean>()
    val userInfo = MutableLiveData<UserInfo>()

    val notification = mutableStateOf<Event<String>?>(null)

    init {
//        firebaseAuth.signOut()
        val currentUser = firebaseAuth.currentUser
        signedIn.value = currentUser != null
        if (currentUser != null) {
            inProgress.value = true // Indicate that loading has started
            userRepository.getUserData(currentUser.uid) { userInfo, exception ->
                inProgress.value = false // Indicate that loading has finished
                if (userInfo != null) {
                    this.userInfo.value = userInfo
                    notification.value = Event("User data retrieved successfully")
                } else {
                    // Handle the error case
                    handleException(exception)
                }
            }
        } else {
            inProgress.value = false // There's no user, so no operation is in progress
        }
    }

    fun signup(username: String, email: String, password: String) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill in all fields")
            return
        }
        inProgress.value = true

        userRepository.checkIfUsernameExists(username) { exists ->
            if (exists) {
                handleException(null, "Username already exists")
                inProgress.value = false
            } else {
                userRepository.createUser(email, password, username) { success, exception ->
                    if (success) {
                        signedIn.value = true
                    } else {
                        handleException(exception, "Signup failed")
                    }
                    inProgress.value = false
                }
            }
        }
    }

    fun login(email: String, password: String) {
        inProgress.value = true

        userRepository.login(email, password) { success, exception ->
            inProgress.value = false
            if (success) {
                signedIn.value = true
//                notification.value = Event("Login Successful")
            } else {
                handleException(exception)
            }
        }
    }

    fun onLogout() {
        firebaseAuth.signOut()
        signedIn.value = false
        userInfo.value = null
        notification.value = Event("Logged out")
//        searchedPosts.value = listOf()
//        postsFeed.value = listOf()
//        comments.value = listOf()
    }

    fun updateProfileData(name: String,username: String,bio: String){
        userRepository.createOrUpdateProfile(name, username, bio )
    }

    fun uploadImage(uri: Uri) {
        userRepository.uploadImage(uri,
            onProgress = { isLoading ->
                inProgress.value = isLoading
            },
            onSuccess = { downloadUri ->
                // Handle successful upload, update the UI as needed
            },
            onFailure = { exception ->
                handleException(exception)
            }
        )
    }

    private fun handleException(exception: Exception? = null, customMessage: String? = null) {
        exception?.printStackTrace()
        val message =  exception?.localizedMessage ?: customMessage ?: "An unknown error occurred"
        notification.value = Event(message)
        exception?.let { Log.e("SignupError", message, it) }

    }


}
