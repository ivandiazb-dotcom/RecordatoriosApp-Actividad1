package com.example.recordatoriosapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recordatoriosapp.data.ReminderOrder
import com.example.recordatoriosapp.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onVolverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Preferencias") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Modo oscuro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Modo oscuro",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Aplicar tema oscuro en la aplicación",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = preferences.darkModeEnabled,
                    onCheckedChange = viewModel::changeDarkMode
                )
            }

            HorizontalDivider()

            // 2. Mostrar completados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mostrar completados",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Incluir tareas finalizadas en el listado",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = preferences.showCompleted,
                    onCheckedChange = viewModel::changeShowCompleted
                )
            }

            HorizontalDivider()

            // 3. Criterio de orden
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Ordenar recordatorios por",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = preferences.reminderOrder == ReminderOrder.BY_DATE,
                        onClick = { viewModel.changeReminderOrder(ReminderOrder.BY_DATE) },
                        label = { Text("Fecha") }
                    )
                    FilterChip(
                        selected = preferences.reminderOrder == ReminderOrder.BY_PRIORITY,
                        onClick = { viewModel.changeReminderOrder(ReminderOrder.BY_PRIORITY) },
                        label = { Text("Prioridad") }
                    )
                }
            }
        }
    }
}