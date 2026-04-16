package com.example.mymobileapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mymobileapp.MyMobileAppApplication
import com.example.mymobileapp.data.AIRepository
import com.example.mymobileapp.data.Note
import com.example.mymobileapp.data.NoteDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(private val noteDao: NoteDao) : ViewModel() {

    private val aiRepository = AIRepository()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()

    fun clearAiError() {
        _aiError.value = null
    }

    val allNotes: StateFlow<List<Note>> = noteDao.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            noteDao.insertNote(Note(title = title, content = content))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            noteDao.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }

    suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }

    suspend fun generateTitle(content: String): String? {
        _isAiLoading.value = true
        _aiError.value = null
        return try {
            val result = aiRepository.generateTitle(content)
            if (result == null) {
                _aiError.value = "Failed to generate title. Empty response."
            }
            result
        } catch (e: Exception) {
            _aiError.value = "AI Error: ${e.localizedMessage ?: "Unknown error"}"
            null
        } finally {
            _isAiLoading.value = false
        }
    }

    suspend fun summarizeContent(content: String): String? {
        _isAiLoading.value = true
        _aiError.value = null
        return try {
            val result = aiRepository.summarizeNote(content)
            if (result == null) {
                _aiError.value = "Failed to summarize note. Empty response."
            }
            result
        } catch (e: Exception) {
            _aiError.value = "AI Error: ${e.localizedMessage ?: "Unknown error"}"
            null
        } finally {
            _isAiLoading.value = false
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MyMobileAppApplication)
                AppViewModel(application.database.noteDao())
            }
        }
    }
}