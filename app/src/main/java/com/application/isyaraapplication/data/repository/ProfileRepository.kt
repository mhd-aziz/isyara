package com.application.isyaraapplication.data.repository

import android.net.Uri
import com.application.isyaraapplication.core.State
import com.application.isyaraapplication.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class ProfileRepository @Inject constructor(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    storage: FirebaseStorage
) {
    private val userCollection = firestore.collection("users")
    private val storageReference = storage.reference

    fun loadUserProfile(): Flow<State<User>> = flow {
        emit(State.Loading)
        try {
            val user = auth.currentUser
            if (user != null) {
                val document = userCollection.document(user.uid).get().await()
                val userProfile = document.toObject(User::class.java)?.copy(
                    uid = user.uid,
                    username = user.displayName,
                    email = user.email,
                    photoUrl = user.photoUrl?.toString()
                ) ?: User(
                    uid = user.uid,
                    username = user.displayName,
                    email = user.email,
                    photoUrl = user.photoUrl?.toString()
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
        bio: String,
        photoUri: Uri?
    ): State<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                val downloadedPhotoUrl = if (photoUri != null) {
                    uploadProfilePhoto(user.uid, photoUri)
                } else {
                    user.photoUrl?.toString()
                }

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(downloadedPhotoUrl?.toUri())
                    .build()
                user.updateProfile(profileUpdates).await()

                val userProfileData = hashMapOf(
                    "fullName" to fullName,
                    "phoneNumber" to phoneNumber,
                    "bio" to bio,
                    "username" to username,
                    "photoUrl" to downloadedPhotoUrl
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

    private suspend fun uploadProfilePhoto(uid: String, photoUri: Uri): String {
        val photoRef = storageReference.child("profile_pictures/$uid.jpg")
        photoRef.putFile(photoUri).await()
        return photoRef.downloadUrl.await().toString()
    }
}