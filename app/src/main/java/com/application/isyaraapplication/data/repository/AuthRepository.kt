package com.application.isyaraapplication.data.repository

import com.application.isyaraapplication.core.State
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser
    private var authStateListener: AuthStateListener? = null

    suspend fun registerUser(email: String, password: String): State<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            State.Success(Unit)
        } catch (e: Exception) {
            State.Error(e.localizedMessage ?: "Registrasi gagal.")
        }
    }

    suspend fun loginUser(email: String, password: String): State<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            State.Success(Unit)
        } catch (e: Exception) {
            State.Error(e.localizedMessage ?: "Login gagal.")
        }
    }

    suspend fun signInWithGoogle(idToken: String): State<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            State.Success(Unit)
        } catch (e: Exception) {
            State.Error(e.localizedMessage ?: "Login dengan Google gagal.")
        }
    }

    suspend fun sendPasswordResetEmail(email: String): State<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            State.Success(Unit)
        } catch (e: Exception) {
            State.Error(e.localizedMessage ?: "Gagal mengirim email reset password.")
        }
    }

    suspend fun updatePassword(oldPassword: String, newPassword: String): State<Unit> {
        return try {
            val user = auth.currentUser
            if (user?.email == null) {
                return State.Error("Pengguna tidak ditemukan atau tidak memiliki email terdaftar.")
            }
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()

            State.Success(Unit)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Password lama yang Anda masukkan salah."
                else -> e.localizedMessage ?: "Gagal memperbarui password."
            }
            State.Error(errorMessage)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun addAuthStateListener(listener: (FirebaseAuth) -> Unit) {
        authStateListener = AuthStateListener { firebaseAuth ->
            listener(firebaseAuth)
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    fun removeAuthStateListener() {
        authStateListener?.let {
            auth.removeAuthStateListener(it)
        }
    }
}