package com.example.recordatoriosapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recordatoriosapp.data.ReminderOrder
import com.example.recordatoriosapp.data.UserPreferences
import com.example.recordatoriosapp.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = repository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences()
        )

    fun changeDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(enabled)
        }
    }

    fun changeShowCompleted(show: Boolean) {
        viewModelScope.launch {
            repository.setShowCompleted(show)
        }
    }

    fun changeReminderOrder(order: ReminderOrder) {
        viewModelScope.launch {
            repository.setReminderOrder(order)
        }
    }

    companion object {
        fun provideFactory(
            repository: UserPreferencesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(repository) as T
            }
        }
    }
}