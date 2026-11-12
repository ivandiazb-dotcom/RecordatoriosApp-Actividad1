package com.example.recordatoriosapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CampoTextoRecordatorio(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    mensajeError: String = "",
    minLines: Int = 1,
    modifier: Modifier = Modifier // Modificador por defecto
) {
    // Es un componente controlado: no guarda su propio estado, lo recibe (value)
    // y avisa cuando el usuario escribe (onValueChange).
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError, // Pinta el borde de rojo si hay error
        supportingText = {
            // Muestra el mensaje de texto explícito si hay error (accesibilidad)
            if (isError && mensajeError.isNotEmpty()) {
                Text(text = mensajeError)
            }
        },
        minLines = minLines,
        modifier = modifier.fillMaxWidth()
    )
}