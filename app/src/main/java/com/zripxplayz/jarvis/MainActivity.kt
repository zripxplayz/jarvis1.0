package com.zripxplayz.jarvis

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private val micPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
    private val speechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!text.isNullOrBlank()) vm.onUserText(text)
    }
    private lateinit var vm: JarvisViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = JarvisViewModel(this)
        setContent { JarvisApp(vm, ::startListening) }
    }

    private fun startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            micPermission.launch(Manifest.permission.RECORD_AUDIO); return
        }
        speechLauncher.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening for JARVIS…")
        })
    }
}

@Composable
fun JarvisApp(vm: JarvisViewModel, onMic: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(vm.hasApiKey()) }
    val messages by vm.messages.collectAsState()

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column { Text("JARVIS", style=MaterialTheme.typography.headlineMedium); Text("Personal AI Assistant", style=MaterialTheme.typography.bodySmall) }
                    TextButton(onClick={showSettings=true}) { Text("Settings") }
                }
                Spacer(Modifier.height(12.dp))
                if (!vm.hasApiKey()) {
                    ApiKeyCard(vm::saveApiKey)
                }
                LazyColumn(Modifier.weight(1f).fillMaxWidth(), reverseLayout=true, verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    items(messages.reversed()) { msg -> Card(shape=RoundedCornerShape(16.dp)) { Text(msg, Modifier.padding(12.dp)) } }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically) {
                    OutlinedTextField(input,{input=it},Modifier.weight(1f),placeholder={Text("Ask JARVIS…")},singleLine=true)
                    Spacer(Modifier.width(8.dp))
                    Button(onClick={ if(input.isNotBlank()){vm.onUserText(input); input=""} }) { Text("Send") }
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick=onMic) { Text("🎙") }
                }
            }
        }
    }
    if (showSettings) AlertDialog(
        onDismissRequest={showSettings=false},
        title={Text("Gemini API Key")},
        text={Text(if(vm.hasApiKey()) "API key is saved securely on this device. Use the key field below to replace it." else "Add your Google Gemini API key to enable AI responses.")},
        confirmButton={TextButton(onClick={showSettings=false}){Text("Done")}}
    )
}

@Composable
private fun ApiKeyCard(save:(String)->Unit) {
    var key by remember { mutableStateOf("") }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("First-time setup", style=MaterialTheme.typography.titleLarge)
            Text("Enter your Gemini API key. It stays on this device and is never sent as chat content.")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(key,{key=it},Modifier.fillMaxWidth(),singleLine=true)
            Spacer(Modifier.height(8.dp))
            Button(onClick={if(key.isNotBlank()) save(key.trim())}) { Text("Save API Key") }
        }
    }
}
