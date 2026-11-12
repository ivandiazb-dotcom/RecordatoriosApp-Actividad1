package com.example.recordatoriosapp.viewmodel

data class CrearRecordatorioUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val prioridad: String = "",
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val enviando: Boolean = false,
    val guardado: Boolean = false,
    val errorGeneral: String? = null
) {
     val formularioValido: Boolean
        get() = titulo.trim().length >= 4 &&
                descripcion.length <= 200 &&
                categoria.isNotEmpty() &&
                prioridad.isNotEmpty()
}