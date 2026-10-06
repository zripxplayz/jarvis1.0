package com.zripxplayz.jarvis

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class JarvisViewModel(private val context: Context) : ViewModel() {
    private val prefs: SharedPreferences = context.getSharedPreferences("jarvis_secure_prefs", Context.MODE_PRIVATE)
    private val _messages = MutableStateFlow<List<String>>(listOf("JARVIS online. API key required for Gemini responses."))
    val messages = _messages.asStateFlow()

    fun hasApiKey(): Boolean = !prefs.getString("gemini_api_key", null).isNullOrBlank()

    fun saveApiKey(key: String) {
        prefs.edit().putString("gemini_api_key", key).apply()
        _messages.value = listOf("Gemini API key saved on this device.", *_messages.value)
    }

    fun onUserText(text: String) {
        _messages.value = listOf("You: $text", *_messages.value)
        if (!hasApiKey()) {
            _messages.value = listOf("JARVIS: Please add your Gemini API key in Settings first.", *_messages.value)
            return
        }
        // Deliberately kept behind a tool/permission layer. AI and device actions must not bypass permissions.
        _messages.value = listOf("JARVIS: Gemini integration endpoint is ready for the next implementation stage.", *_messages.value)
    }
}
