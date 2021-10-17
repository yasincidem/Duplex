package com.yasincidem.duplex.feature.ui.chat

import android.net.Uri
import android.text.format.DateUtils
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.yasincidem.duplex.feature.ui.search.User
import com.yasincidem.duplex.navigation.CHAT_OTHER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatViewModel @Inject constructor(
    handle: SavedStateHandle,
) : ViewModel() {

    private val chatOtherId = handle.get<String>(CHAT_OTHER_ID)!!

    private val fireStore = Firebase.firestore
    private val fireStorage = Firebase.storage

    private val chatRef = fireStore.collection("chats")

    val currentUserId = Firebase.auth.currentUser?.uid

    val otherUser = MutableStateFlow<User?>(null)

    private var _timerStateFlow = MutableStateFlow(0)
    val timerStateFlow: StateFlow<Int> = _timerStateFlow

    private var _timerEnabled = MutableStateFlow(true)

    private var chatSendHistory by mutableStateOf<List<Voice>>(emptyList())
    private var chatReceiveHistory by mutableStateOf<List<Voice>>(emptyList())

    var chatHistory by mutableStateOf<List<Voice>>(emptyList())

    init {

        snapshotFlow {
            chatReceiveHistory to chatSendHistory
        }.mapLatest { pair ->
            (pair.first + pair.second).sortedByDescending { it.sentAt }
        }.onEach {
            chatHistory = it
        }.launchIn(viewModelScope)

        Log.i("qqqqqq", "$currentUserId::::::::$chatOtherId")
        viewModelScope.launch {
            getSendChatHistory().collect {
                chatSendHistory = it
            }
        }

        viewModelScope.launch {
            getReceiveChatHistory().collect {
                chatReceiveHistory = it
            }
        }

        viewModelScope.launch {
            getOtherUserData(chatOtherId).collect {
                otherUser.value = it
            }
        }
    }

    private suspend fun getSendChatHistory(): Flow<List<Voice>> = callbackFlow {
        val ref =
            fireStore.collection("chats/${Firebase.auth.currentUser?.uid}/to/$chatOtherId/voices")
        val subscription = ref.addSnapshotListener { snapshot, _ ->
            snapshot?.documents?.forEach {
                Log.i("rererere", it.data.toString())
            }
            if (snapshot?.isEmpty == false) {
                trySend(snapshot.toObjects(Voice::class.java))
            }
        }

        awaitClose {
            subscription.remove()
        }
    }

    private suspend fun getReceiveChatHistory(): Flow<List<Voice>> = callbackFlow {
        val ref =
            fireStore.collection("chats/$chatOtherId/to/${Firebase.auth.currentUser?.uid}/voices")
        val subscription = ref.addSnapshotListener { snapshot, _ ->
            snapshot?.documents?.forEach {
                Log.i("rererere", it.data.toString())
            }
            if (snapshot?.isEmpty == false) {
                trySend(snapshot.toObjects(Voice::class.java))
            }
        }

        awaitClose {
            subscription.remove()
        }
    }

    private fun setUpTimer() = (0 until Int.MAX_VALUE).asFlow()
        .onEach { delay(1_000) }
        .conflate()

    fun startTimer() = viewModelScope.launch {
        _timerEnabled.emit(true)
        setUpTimer()
            .takeWhile { _timerEnabled.value }
            .collect { _timerStateFlow.emit(it) }
    }

    fun stopTimer() = viewModelScope.launch {
        _timerStateFlow.emit(0)
        _timerEnabled.emit(false)
    }

    fun sendVoice(fileName: String) = viewModelScope.launch {
        val voiceId = UUID.randomUUID().toString()

        fireStorage.reference.child("voices").child(voiceId)
            .putFile(Uri.fromFile(File(fileName)))
            .await()

        fireStore
            .collection("chats/${Firebase.auth.currentUser?.uid}/to/$chatOtherId/voices")
            .document()
            .set(hashMapOf(
                "voiceId" to voiceId,
                "sentAt" to FieldValue.serverTimestamp(),
                "by" to Firebase.auth.currentUser?.uid
            ))
            .addOnSuccessListener {
                // loadingState.value = false
                // successState.value = true
            }.await()
    }

    suspend fun getVoiceUrl(voiceId: String): String {
        val ref = fireStorage.reference.child("voices").child(voiceId)
        return ref.downloadUrl.await().toString()
    }

    private suspend fun getOtherUserData(userId: String): Flow<User?> = callbackFlow {
        val ref = chatRef.document("${Firebase.auth.currentUser?.uid}/to/$userId")

        val subscription = ref.addSnapshotListener { snapshot, a ->
            if (snapshot?.exists() == true) {
                trySend(snapshot.toObject(User::class.java))
            }
        }

        awaitClose { subscription.remove() }
    }

    fun prettifyTimer(seconds: Int) = DateUtils.formatElapsedTime(seconds.toLong())
}

data class Voice(
    val voiceId: String = "",
    @ServerTimestamp val sentAt: Date = Date(),
    val by: String = ""
)
