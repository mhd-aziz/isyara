package com.application.isyaraapplication.data.repository

import com.application.isyaraapplication.core.State
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslateRepository @Inject constructor(
    private val generativeModel: GenerativeModel
) {
    suspend fun spellCheck(text: String): State<String> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt =
                    "Koreksi ejaan dan tata bahasa dari kumpulan huruf berikut menjadi kata atau kalimat yang paling dekat dengan kata-kata yang benar dalam Bahasa Indonesia. Prioritaskan huruf terlebih dahulu, lalu kata. Jangan menambahkan kata-kata yang tidak ada. Hanya kembalikan teks yang sudah dikoreksi tanpa penjelasan tambahan. Teks: '$text'"

                val response = generativeModel.generateContent(prompt)
                val correctedText =
                    response.text ?: return@withContext State.Error("Tidak ada respons dari API.")

                State.Success(correctedText.trim())
            } catch (e: Exception) {
                State.Error(e.localizedMessage ?: "Pengecekan ejaan gagal.")
            }
        }
    }
}