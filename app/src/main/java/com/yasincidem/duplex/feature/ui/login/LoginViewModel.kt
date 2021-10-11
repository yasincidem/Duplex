package com.yasincidem.duplex.feature.ui.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yasincidem.duplex.common.livedata.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    var loadingState = MutableLiveData<Boolean?>()
        private set
    var successState = LiveEvent<Boolean?>()
        private set

    fun signWithCredentialAndSaveData(
        credential: AuthCredential,
        userId: String,
        account: GoogleSignInAccount,
        username: String,
        phoneNumber: String,
    ) = viewModelScope.launch {
        try {
            Firebase.auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    val userData = hashMapOf(
                        "name" to account.displayName,
                        "email" to account.email,
                        "photoUrl" to account.photoUrl?.toString(),
                        "username" to username,
                        "phoneNumber" to phoneNumber,
                    )
                    saveUserData(
                        userId,
                        userData
                    )
                }.addOnFailureListener {
                    setError()
                }.await()
        } catch (e: Exception) {
            setError()
        }
    }

    private fun saveUserData(
        userId: String,
        userData: HashMap<String, String?>,
    ) = viewModelScope.launch {
        try {
            val docRef = Firebase.firestore.collection("users")
            docRef.document(userId).set(userData)
                .addOnSuccessListener {
                    loadingState.value = false
                    successState.value = true
                }.addOnFailureListener {
                    setError()
                }.await()
        } catch (e: Exception) {
            setError()
        }
    }

    fun getProfileInformation(@ApplicationContext context: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun getLastSignInAccount(@ApplicationContext context: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun isLoggedIn() = Firebase.auth.currentUser != null

    fun getCurrentUserOrNull() = Firebase.auth.currentUser

    fun logout() = Firebase.auth.signOut()

    fun setLoadingLiveData(isLoading: Boolean) {
        loadingState.value = isLoading
    }

    private fun setError() {
        loadingState.value = false
        successState.value = false
    }
}
