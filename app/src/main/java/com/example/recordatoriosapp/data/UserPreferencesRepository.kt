package com.example.recordatoriosapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

enum class ReminderOrder {
    BY_DATE,
    BY_PRIORITY
}

data class UserPreferences(
    val darkModeEnabled: Boolean = false,
    val showCompleted: Boolean = true,
    val reminderOrder: ReminderOrder = ReminderOrder.BY_DATE
)

private object PreferenceKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
    val REMINDER_ORDER = stringPreferencesKey("reminder_order")
}

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val preferences: Flow<UserPreferences> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { values ->
            val savedOrder = values[PreferenceKeys.REMINDER_ORDER]
            val order = ReminderOrder.entries
                .firstOrNull { it.name == savedOrder }
                ?: ReminderOrder.BY_DATE

            UserPreferences(
                darkModeEnabled = values[PreferenceKeys.DARK_MODE] ?: false,
                showCompleted = values[PreferenceKeys.SHOW_COMPLETED] ?: true,
                reminderOrder = order
            )
        }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[PreferenceKeys.DARK_MODE] = enabled }
    }

    suspend fun setShowCompleted(showCompleted: Boolean) {
        dataStore.edit { it[PreferenceKeys.SHOW_COMPLETED] = showCompleted }
    }

    suspend fun setReminderOrder(order: ReminderOrder) {
        dataStore.edit { it[PreferenceKeys.REMINDER_ORDER] = order.name }
    }
}