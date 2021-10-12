package com.yasincidem.duplex.feature.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yasincidem.duplex.feature.ui.search.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val chatsRef = Firebase.firestore.collection("chats")

    val chats = MutableStateFlow<List<User>>(emptyList())
    val loadingState = MutableLiveData<Boolean?>(null)

    init {
        viewModelScope.launch {
            getChats().collect {
                chats.value = it
            }
        }
    }

    private fun getChats(): Flow<List<User>> = callbackFlow {
        loadingState.value = true
        val ref = chatsRef.document(currentUserId()).collection("to")

        val subscription = ref.addSnapshotListener { snapshot, _ ->
            if (snapshot?.isEmpty == false) {
                trySend(snapshot.toObjects(User::class.java))
            }
        }

        awaitClose {
            loadingState.value = false
            subscription.remove()
        }
    }

    fun currentUserId() = Firebase.auth.currentUser?.uid ?: ""

    fun isLoggedIn() = Firebase.auth.currentUser != null

    fun logout() = Firebase.auth.signOut()
}
