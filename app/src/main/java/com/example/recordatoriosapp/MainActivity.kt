package com.example.recordatoriosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recordatoriosapp.model.*
import com.example.recordatoriosapp.ui.theme.RecordatoriosAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecordatoriosAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaRecordatorios(DatosSimulados.listaRecordatorios)
                }
            }
        }
    }
}

@Composable
fun PantallaRecordatorios(recordatorios: List<Recordatorio>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(recordatorios) { recordatorio ->
            ItemRecordatorio(recordatorio)
        }
    }
}

@Composable
fun ItemRecordatorio(recordatorio: Recordatorio) {
    // Extracción segura del color desde el Map; si no existe, usa Gris.
    val colorCategoria = DatosSimulados.coloresCategoria[recordatorio.categoria] ?: Color.Gray

    // Expresión exhaustiva gracias a la sealed class
    val textoEstado = when (recordatorio.estado) {
        is EstadoRecordatorio.Pendiente -> "Pendiente"
        is EstadoRecordatorio.EnProgreso -> "En Progreso"
        is EstadoRecordatorio.Completado -> "Completado"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = recordatorio.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = recordatorio.categoria,
                    color = Color.White,
                    modifier = Modifier
                        .background(colorCategoria, shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // let asegura que este bloque de texto solo se dibuje si hay descripción
            recordatorio.descripcion?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Prioridad: ${recordatorio.prioridad.javaClass.simpleName}", style = MaterialTheme.typography.labelLarge)
                Text(text = "Estado: $textoEstado", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VistaPreviaPantalla() {
    RecordatoriosAppTheme {
        PantallaRecordatorios(DatosSimulados.listaRecordatorios)
    }
}