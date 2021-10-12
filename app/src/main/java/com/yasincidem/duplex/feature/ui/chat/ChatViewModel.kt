package com.yasincidem.duplex.feature.ui.chat

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.yasincidem.duplex.feature.ui.search.User
import com.yasincidem.duplex.navigation.CHAT_OTHER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    handle: SavedStateHandle,
) : ViewModel() {

    private val chatOtherId = handle.get<String>(CHAT_OTHER_ID)!!

    private val fireStore = Firebase.firestore
    private val fireStorage = Firebase.storage

    private val chatRef = fireStore.collection("chats")

    val otherUser = MutableStateFlow<User?>(null)

    init {
        viewModelScope.launch {
            getOtherUserData(chatOtherId).collect {
                otherUser.value = it
            }
        }
    }

    fun sendVoice(fileName: String) = viewModelScope.launch {
        val voiceId = UUID.randomUUID().toString()

        fireStorage.reference.child("voices").child(voiceId)
            .putFile(Uri.fromFile(File(fileName)))
            .await()

        fireStore
            .collection("chats/${Firebase.auth.currentUser?.uid}/to/$chatOtherId/voices")
            .document()
            .set(hashMapOf("voiceId" to voiceId))
            .addOnSuccessListener {
                // loadingState.value = false
                // successState.value = true
            }.await()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getOtherUserData(userId: String): Flow<User?> = callbackFlow {
        val ref = chatRef.document("${Firebase.auth.currentUser?.uid}/to/$userId")

        val subscription = ref.addSnapshotListener { snapshot, _ ->
            if (snapshot?.exists() == true) {
                trySend(snapshot.toObject(User::class.java))
            }
        }

        awaitClose { subscription.remove() }
    }
}
