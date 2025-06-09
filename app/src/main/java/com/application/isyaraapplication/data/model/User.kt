package com.application.isyaraapplication.data.model

data class User(
    val uid: String = "",
    val username: String? = "",
    val email: String? = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val bio: String = ""
)