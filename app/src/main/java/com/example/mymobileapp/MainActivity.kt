package com.example.mymobileapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mymobileapp.ui.NoteListScreen
import com.example.mymobileapp.ui.theme.MyMobileAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyMobileAppTheme {
                NoteListScreen(
                    onNavigateToAddNote = { 
                        val intent = Intent(this, NoteEntryActivity::class.java)
                        startActivity(intent)
                    },
                    onNavigateToEditNote = { id -> 
                        val intent = Intent(this, NoteEntryActivity::class.java)
                        intent.putExtra("note_id", id)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}