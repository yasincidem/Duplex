package com.yasincidem.duplex.feature.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    fun signWithCredential(credential: AuthCredential) = viewModelScope.launch {
        try {
            Firebase.auth.signInWithCredential(credential).await()
        } catch (e: Exception) {
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
}
