package com.example.recordatoriosapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recordatoriosapp.model.EstadoRecordatorio
import com.example.recordatoriosapp.model.Recordatorio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRecordatorioScreen(
    recordatorio: Recordatorio?,
    onVolverClick: () -> Unit,
    onAlternarCompletado: (Recordatorio) -> Unit,
    onEliminarClick: (Recordatorio) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Tarea") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (recordatorio != null) {
                val estaCompletado = recordatorio.estado is EstadoRecordatorio.Completado

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = recordatorio.titulo,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (!recordatorio.descripcion.isNullOrBlank()) {
                                Text(
                                    text = recordatorio.descripcion,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            HorizontalDivider()
                            Text(text = "Categoría: ${recordatorio.categoria}")
                            Text(text = "Prioridad: ${recordatorio.prioridad}")
                            Text(
                                text = "Estado: ${if (estaCompletado) "Completado" else "Pendiente"}",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Button(
                        onClick = { onAlternarCompletado(recordatorio) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (estaCompletado) "Marcar como Pendiente" else "Marcar como Completado"
                        )
                    }

                    OutlinedButton(
                        onClick = { onEliminarClick(recordatorio) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar recordatorio")
                    }
                }
            } else {
                Text("Recordatorio no encontrado")
            }
        }
    }
}