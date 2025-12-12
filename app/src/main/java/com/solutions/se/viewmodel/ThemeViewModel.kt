package com.solutions.se.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.solutions.se.repository.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = ThemePreferences(application)
    val darkThemeFlow = prefs.darkThemeFlow
        .stateIn(viewModelScope, SharingStarted.Companion.Lazily, false)

    fun toggleDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            prefs.setDarkTheme(isDark)
        }
    }
}