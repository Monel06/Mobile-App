package com.example.mymobileapp.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend

class AIRepository {

    private val model = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel(
        modelName = "gemini-2.5-flash-lite"
    )

    suspend fun generateTitle(content: String): String? {
        return try {
            val prompt = """
            Создай заголовок для заметки.
            Требования:
            - только заголовок
            - максимум 4 слова
            - одна строка
            - без пояснений
            - без кавычек
            - отвечай на английском

            Текст:
            $content
        """.trimIndent()

            val response = model.generateContent(prompt)
            val rawTitle = response.text
                ?.trim()
                ?.lineSequence()
                ?.firstOrNull()
                ?.replace("\"", "")
                ?.replace(".", "")
                ?.trim()

            if (rawTitle.isNullOrBlank()) {
                "Заметка"
            } else {
                rawTitle.split(" ")
                    .take(4)
                    .joinToString(" ")
            }
        } catch (e: Exception) {
            Log.e("AIRepository", "generateTitle error: ${e.message}", e)
            "Заметка"
        }
    }

    suspend fun summarizeNote(content: String): String? {
        return try {
            val prompt = """
            Кратко суммируй текст заметки.

            Требования:
            - только резюме
            - без кавычек
            - без заголовка
            - без пояснений
            - без лишних слов
            - максимум 2 коротких предложения
            - сохрани только главную мысль
            - отвечай на английском

            Текст:
            $content
        """.trimIndent()

            val response = model.generateContent(prompt)
            response.text
                ?.trim()
                ?.replace("\"", "")
                ?.replace("«", "")
                ?.replace("»", "")
        } catch (e: Exception) {
            Log.e("AIRepository", "summarizeNote error: ${e.message}", e)
            null
        }
    }
}