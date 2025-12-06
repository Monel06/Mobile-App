package com.example.mymobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mymobileapp.ui.NoteEntryScreen
import com.example.mymobileapp.ui.theme.MyMobileAppTheme

class NoteEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val noteId = intent.getIntExtra("note_id", -1)
        val validNoteId = if (noteId == -1) null else noteId

        setContent {
            MyMobileAppTheme {
                NoteEntryScreen(
                    navigateBack = { finish() },
                    noteId = validNoteId
                )
            }
        }
    }
}