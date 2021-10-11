package com.yasincidem.duplex.feature.ui.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val usersRef = Firebase.firestore.collection("users")

    fun searchContact(
        searchKey: String,
    ) = viewModelScope.launch {
        usersRef
            .orderBy("username")
            .startAt(searchKey)
            .endAt(searchKey + '\uf8ff')
            .get()
            .addOnSuccessListener {
                Log.i("tttrtrtr size", "${it.size()}")

                for (document in it.documents) {
                    Log.i("tttrtrtr", "${document.id} => ${document.data}")
                }
            }.await()
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
