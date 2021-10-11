package com.yasincidem.duplex.feature.ui.search

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
import javax.inject.Inject

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

    fun getProfileInformation(@ApplicationContext context: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun getLastSignInAccount(@ApplicationContext context: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun isLoggedIn() = Firebase.auth.currentUser != null

    fun getCurrentUserOrNull() = Firebase.auth.currentUser

    fun logout() = Firebase.auth.signOut()

    fun updateSearchQuery(query: String) {
        this.query.value = query
    }
}

data class User(
    val name: String = "",
    val phoneNumber: String = "",
    val photoUrl: String = "",
    val email: String = "",
    val username: String = "",
)
