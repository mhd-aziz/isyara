package com.application.isyaraapplication.features.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.isyaraapplication.BuildConfig
import com.application.isyaraapplication.data.model.Message
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val systemPrompt = """
        Anda adalah Isyara Bot, asisten AI virtual di dalam aplikasi "Isyara".
        Tujuan utama Anda adalah untuk membantu pengguna mempelajari bahasa isyarat di Indonesia, khususnya Sistem Bahasa Isyarat Indonesia (SIBI) dan Bahasa Isyarat Indonesia (BISINDO).
        Aplikasi Isyara memiliki fitur utama yaitu "Kamus Isyarat" untuk mencari kosakata.

        Aturan Perilaku:
        1.  Fokus Utama: Selalu fokus pada topik bahasa isyarat. Jawab pertanyaan tentang kosakata, perbedaan SIBI dan BISINDO, sejarah, atau budaya Tuli.
        2.  Tolak Topik Lain: Jika pengguna bertanya di luar topik bahasa isyarat, tolak dengan sopan. Contoh: "Maaf, saya hanya bisa membantu pertanyaan seputar bahasa isyarat. Apakah ada yang ingin Anda tanyakan tentang SIBI atau BISINDO?"
        3.  Rekomendasikan Fitur Kamus: Jika pengguna bertanya tentang isyarat sebuah kata (misalnya: "apa isyaratnya 'belajar'?"), setelah menjawab, secara proaktif rekomendasikan: "Untuk melihat visual gerakannya, Anda bisa cek langsung di fitur Kamus Isyarat kami, lho."
        4.  Gaya Bahasa: Gunakan bahasa yang ramah, positif, dan memotivasi.
    """.trimIndent()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-latest",
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = content(role = "system") { text(systemPrompt) }
    )

    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "user") { text("Halo, perkenalkan dirimu.") },
            content(role = "model") { text("Halo! Saya Isyara Bot, siap membantu Anda menjelajahi dunia bahasa isyarat. Ada yang bisa saya bantu?") }
        )
    )

    init {
        val initialMessage = chat.history.lastOrNull()?.parts?.firstOrNull()?.asTextOrNull()
        _messages.value =
            listOf(Message(initialMessage ?: "Halo! Ada yang bisa saya bantu?", false))
    }

    fun sendMessage(text: String) {
        _messages.value += Message(text, true)

        viewModelScope.launch {
            _messages.value += Message("...", false)

            try {
                val response = chat.sendMessage(text)

                _messages.value = _messages.value.dropLast(1)

                response.text?.let {
                    _messages.value += Message(it, false)
                }
            } catch (_: Exception) {
                _messages.value = _messages.value.dropLast(1)
                _messages.value += Message(
                    "Maaf, sepertinya ada sedikit gangguan. Mohon coba lagi.",
                    false
                )
            }
        }
    }
}