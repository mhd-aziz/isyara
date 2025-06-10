package com.application.isyaraapplication.data.model

data class DictionaryItem(
    val name: String,
    val url: String,
    val type: ItemType
)

enum class ItemType {
    IMAGE,
    VIDEO
}