package com.example.recordatoriosapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recordatoriosapp.ui.components.CampoTextoRecordatorio
import com.example.recordatoriosapp.viewmodel.CrearRecordatorioUiState
import com.example.recordatoriosapp.viewmodel.RecordatoriosViewModel // Importante agregar esto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRecordatorioScreen(
    uiState: CrearRecordatorioUiState,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onGuardarClick: () -> Unit,
    viewModel: RecordatoriosViewModel // Recibimos el ViewModel para pasarle los valores por defecto
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Recordatorio") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CampoTextoRecordatorio(
                value = uiState.titulo,
                onValueChange = onTituloChange,
                label = "Título",
                isError = uiState.errorTitulo != null,
                mensajeError = uiState.errorTitulo ?: ""
            )

            CampoTextoRecordatorio(
                value = uiState.descripcion,
                onValueChange = onDescripcionChange,
                label = "Descripción",
                isError = uiState.errorDescripcion != null,
                mensajeError = uiState.errorDescripcion ?: "",
                minLines = 3
            )

            Button(
                onClick = {
                    // Asignamos valores por defecto para pasar la validación
                    viewModel.seleccionarCategoria("Estudios")
                    viewModel.seleccionarPrioridad("Alta")
                    onGuardarClick()
                },
                // Ahora validamos solo el título para que el botón se habilite
                enabled = uiState.titulo.trim().length >= 4 && !uiState.enviando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.enviando) "Guardando..." else "Guardar Recordatorio")
            }
        }
    }
}