package dev.alexzvn.universalyogaplus.service

import android.annotation.SuppressLint
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object AuthService {
    val flowUser: Flow<FirebaseUser?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                this.trySend(auth.currentUser)
            }

            Firebase.auth.addAuthStateListener(listener)

            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    val user: FirebaseUser?
        get() = Firebase.auth.currentUser

    suspend fun login(email: String, password: String): AuthResult? {
        return try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
        } catch (error: Throwable) {
            null
        }
    }

    fun logout() {
        Firebase.auth.signOut()
    }
}