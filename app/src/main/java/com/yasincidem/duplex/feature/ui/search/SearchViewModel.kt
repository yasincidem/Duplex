package com.yasincidem.duplex.feature.ui.search

import android.content.Context
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yasincidem.duplex.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@ExperimentalCoroutinesApi
@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val usersRef = Firebase.firestore.collection("users")

    val query = MutableStateFlow("")

    fun getUsers(): Flow<List<User>> {
        return query
            .debounce(100)
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
        val docRef = Firebase.firestore.collection("chats")
        docRef.document("${Firebase.auth.currentUser?.uid}/to/${user.id}").set(user.asMap())
            .addOnSuccessListener {
                //loadingState.value = false
                //successState.value = true
            }.await()
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
