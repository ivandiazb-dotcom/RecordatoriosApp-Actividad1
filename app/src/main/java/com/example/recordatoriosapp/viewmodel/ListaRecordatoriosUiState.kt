package com.example.recordatoriosapp.viewmodel

import com.example.recordatoriosapp.model.Recordatorio

sealed interface ListaRecordatoriosUiState {
    data object Cargando : ListaRecordatoriosUiState
    data object Vacia : ListaRecordatoriosUiState
    data class ConDatos(val recordatorios: List<Recordatorio>) : ListaRecordatoriosUiState
    data class Error(val mensaje: String) : ListaRecordatoriosUiState
}