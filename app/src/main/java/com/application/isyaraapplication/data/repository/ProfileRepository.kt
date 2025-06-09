package com.application.isyaraapplication.data.repository

import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore
) {
    private val userCollection = firestore.collection("users")

    fun loadUserProfile(): Flow<State<User>> = flow {
        emit(State.Loading)
        try {
            val user = auth.currentUser
            if (user != null) {
                val document = userCollection.document(user.uid).get().await()
                val userProfile = document.toObject(User::class.java)?.copy(
                    uid = user.uid,
                    username = user.displayName,
                    email = user.email
                ) ?: User(
                    uid = user.uid,
                    username = user.displayName,
                    email = user.email
                )
                emit(State.Success(userProfile))
            } else {
                emit(State.Error("Pengguna tidak ditemukan"))
            }
        } catch (e: Exception) {
            emit(State.Error(e.message ?: "Gagal memuat profil"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveUserProfile(
        username: String,
        fullName: String,
        phoneNumber: String,
        bio: String
    ): State<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                user.updateProfile(profileUpdates).await()
                val userProfileData = hashMapOf(
                    "fullName" to fullName,
                    "phoneNumber" to phoneNumber,
                    "bio" to bio,
                    "username" to username
                )

                userCollection.document(user.uid).set(userProfileData).await()
                State.Success(Unit)
            } else {
                State.Error("Pengguna tidak ditemukan")
            }
        } catch (e: Exception) {
            State.Error(e.message ?: "Gagal menyimpan profil")
        }
    }
}