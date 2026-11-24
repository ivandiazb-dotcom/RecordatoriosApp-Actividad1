package com.example.recordatoriosapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recordatoriosapp.ui.components.RecordatorioCard
import com.example.recordatoriosapp.viewmodel.ListaRecordatoriosUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaRecordatoriosScreen(
    uiState: ListaRecordatoriosUiState,
    onCrearClick: () -> Unit,
    onRecordatorioClick: (Long) -> Unit,
    onPreferenciasClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Recordatorios") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                actions = {
                    IconButton(onClick = onPreferenciasClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Preferencias"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearClick) {
                Icon(Icons.Filled.Add, contentDescription = "Crear nuevo")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is ListaRecordatoriosUiState.Cargando -> CircularProgressIndicator()
                is ListaRecordatoriosUiState.Vacia -> {
                    Text(text = "No tienes recordatorios pendientes.", style = MaterialTheme.typography.bodyLarge)
                }
                is ListaRecordatoriosUiState.Error -> {
                    Text(text = uiState.mensaje, color = MaterialTheme.colorScheme.error)
                }
                is ListaRecordatoriosUiState.ConDatos -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                    ) {
                        items(uiState.recordatorios, key = { it.id }) { recordatorio ->
                            RecordatorioCard(
                                titulo = recordatorio.titulo,
                                categoria = recordatorio.categoria,
                                prioridad = recordatorio.prioridad.toString(),
                                estado = recordatorio.estado.toString(),
                                onClick = { onRecordatorioClick(recordatorio.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}