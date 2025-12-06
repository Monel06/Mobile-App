package com.example.mymobileapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
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