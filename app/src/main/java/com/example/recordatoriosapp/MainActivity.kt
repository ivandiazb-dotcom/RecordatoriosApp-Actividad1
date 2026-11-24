package com.example.recordatoriosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatoriosapp.data.AppDatabase
import com.example.recordatoriosapp.data.NetworkMonitor
import com.example.recordatoriosapp.data.RetrofitClient
import com.example.recordatoriosapp.data.RoomRecordatoriosRepository
import com.example.recordatoriosapp.data.UserPreferencesRepository
import com.example.recordatoriosapp.data.userPreferencesDataStore
import com.example.recordatoriosapp.navigation.RecordatoriosNavHost
import com.example.recordatoriosapp.ui.theme.RecordatoriosAppTheme
import com.example.recordatoriosapp.viewmodel.RecordatoriosViewModel
import com.example.recordatoriosapp.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesRepository = UserPreferencesRepository(
            applicationContext.userPreferencesDataStore
        )

        val database = AppDatabase.getInstance(applicationContext)
        val recordatoriosRepository = RoomRecordatoriosRepository(
            reminderDao = database.reminderDao(),
            apiService = RetrofitClient.api
        )
        val networkMonitor = NetworkMonitor(applicationContext)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.provideFactory(preferencesRepository)
            )
            val recordatoriosViewModel: RecordatoriosViewModel = viewModel(
                factory = RecordatoriosViewModel.provideFactory(
                    repository = recordatoriosRepository,
                    networkMonitor = networkMonitor
                )
            )
            val preferences by settingsViewModel.preferences.collectAsStateWithLifecycle()

            RecordatoriosAppTheme(darkTheme = preferences.darkModeEnabled) {
                RecordatoriosNavHost(
                    settingsViewModel = settingsViewModel,
                    viewModel = recordatoriosViewModel
                )
            }
        }
    }
}