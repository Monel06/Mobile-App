package com.example.mymobileapp

import android.app.Application
import com.example.mymobileapp.data.NoteDatabase

class MyMobileAppApplication : Application() {
    val database: NoteDatabase by lazy { NoteDatabase.getDatabase(this) }
}