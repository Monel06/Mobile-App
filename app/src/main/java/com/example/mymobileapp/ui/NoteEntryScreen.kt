package com.example.mymobileapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mymobileapp.data.Note
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEntryScreen(
    navigateBack: () -> Unit,
    noteId: Int? = null,
    viewModel: AppViewModel = viewModel(factory = AppViewModel.Factory)
) {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiError by viewModel.aiError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(aiError) {
        aiError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearAiError()
        }
    }
    
    LaunchedEffect(noteId) {
        if (noteId != null && noteId != -1) {
             val note = viewModel.getNoteById(noteId)
             if (note != null) {
                 title = note.title
                 content = note.content
                 isEditing = true
             }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (isEditing) "Edit Note" else "Add Note") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        scope.launch {
                            val suggestedTitle = viewModel.generateTitle(content)
                            if (suggestedTitle != null) {
                                title = suggestedTitle
                            }
                        }
                    },
                    enabled = content.isNotBlank() && !isAiLoading
                ) {
                    if (isAiLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Suggest Title")
                    }
                }
            }
            
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            
            if (content.length > 50) {
                Button(
                    onClick = {
                        scope.launch {
                            val summary = viewModel.summarizeContent(content)
                            if (summary != null) {
                                content = "$content\n\nSummary:\n$summary"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isAiLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(if (isAiLoading) "Thinking..." else "AI Summarize")
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        if (isEditing && noteId != null) {
                            viewModel.updateNote(Note(id = noteId, title = title, content = content))
                        } else {
                            viewModel.addNote(title, content)
                        }
                        navigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }
}