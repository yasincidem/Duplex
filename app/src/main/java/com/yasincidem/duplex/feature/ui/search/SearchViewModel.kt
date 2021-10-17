package com.yasincidem.duplex.feature.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@ExperimentalCoroutinesApi
@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val usersRef = Firebase.firestore.collection("users")

    val currentUserId = Firebase.auth.currentUser?.uid

    val query = MutableStateFlow("")

    fun getUsers(): Flow<List<User>> {
        return query
            .flatMapLatest { query ->
                getSearchResult(query)
            }
            .flowOn(Dispatchers.IO)
    }

    private suspend fun getSearchResult(query: String): Flow<List<User>> = callbackFlow {
        val ref = usersRef
            .orderBy("username")
            .startAt(query)
            .endAt(query + '\uf8ff')

        val subscription = ref.addSnapshotListener { snapshot, _ ->
            if (snapshot?.isEmpty == false) {
                trySend(snapshot.toObjects(User::class.java))
            }
        }

        awaitClose { subscription.remove() }
    }

    fun createChat(user: User) = viewModelScope.launch {
        Firebase.firestore.collection("chats")
            .document("${currentUserId}/to/${user.id}").set(user.asMap())
            .await()

        currentUserId ?: return@launch
        Firebase.firestore.collection("users")
            .document(currentUserId)
            .get().addOnSuccessListener { snapShot ->
                if (snapShot.exists()) {
                    val me = snapShot.toObject(User::class.java) ?: return@addOnSuccessListener
                    viewModelScope.launch {
                        Firebase.firestore.collection("chats")
                            .document("${user.id}/to/${currentUserId}").set(me.asMap())
                            .await()
                    }
                }
            }
            .await()
    }

    fun isLoggedIn() = Firebase.auth.currentUser != null

    fun logout() = Firebase.auth.signOut()

    fun updateSearchQuery(query: String) {
        this.query.value = query
    }
}

data class User(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val phoneNumber: String = "",
) {
    fun asMap() = hashMapOf(
        "id" to id,
        "name" to name,
        "username" to username,
        "email" to email,
        "photoUrl" to photoUrl,
        "phoneNumber" to phoneNumber,
    )
}
