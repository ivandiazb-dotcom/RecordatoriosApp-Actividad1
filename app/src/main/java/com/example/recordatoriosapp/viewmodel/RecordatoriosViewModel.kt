package com.example.recordatoriosapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatoriosapp.data.RecordatoriosEnMemoriaRepository
import com.example.recordatoriosapp.data.RecordatoriosRepository
import com.example.recordatoriosapp.model.EstadoRecordatorio
import com.example.recordatoriosapp.model.Prioridad
import com.example.recordatoriosapp.model.Recordatorio
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecordatoriosViewModel : ViewModel() {

    // Instanciamos el repositorio directamente aquí para evitar el error de inicio
    private val repository: RecordatoriosRepository = RecordatoriosEnMemoriaRepository()

    private val _listaUiState = MutableStateFlow<ListaRecordatoriosUiState>(ListaRecordatoriosUiState.Cargando)
    val listaUiState: StateFlow<ListaRecordatoriosUiState> = _listaUiState.asStateFlow()

    private val _crearUiState = MutableStateFlow(CrearRecordatorioUiState())
    val crearUiState: StateFlow<CrearRecordatorioUiState> = _crearUiState.asStateFlow()

    init {
        cargarRecordatorios()
    }

    fun cargarRecordatorios() {
        viewModelScope.launch {
            _listaUiState.value = ListaRecordatoriosUiState.Cargando
            delay(1500)

            repository.observarRecordatorios().collect { lista ->
                if (lista.isEmpty()) {
                    _listaUiState.value = ListaRecordatoriosUiState.Vacia
                } else {
                    _listaUiState.value = ListaRecordatoriosUiState.ConDatos(lista)
                }
            }
        }
    }

    fun simularError() {
        _listaUiState.value = ListaRecordatoriosUiState.Error("No se pudo cargar la información. Reintenta más tarde.")
    }

    fun actualizarTitulo(nuevoTitulo: String) {
        val error = if (nuevoTitulo.trim().length < 4) "Mínimo 4 caracteres" else null
        _crearUiState.update { it.copy(titulo = nuevoTitulo, errorTitulo = error) }
    }

    fun actualizarDescripcion(nuevaDesc: String) {
        val error = if (nuevaDesc.length > 200) "Máximo 200 caracteres" else null
        if (nuevaDesc.length <= 200) {
            _crearUiState.update { it.copy(descripcion = nuevaDesc, errorDescripcion = error) }
        }
    }

    fun seleccionarCategoria(categoria: String) {
        _crearUiState.update { it.copy(categoria = categoria) }
    }

    fun seleccionarPrioridad(prioridad: String) {
        _crearUiState.update { it.copy(prioridad = prioridad) }
    }

    fun resetearFormulario() {
        _crearUiState.value = CrearRecordatorioUiState()
    }

    fun guardarRecordatorio() {
        val estadoActual = _crearUiState.value
        if (!estadoActual.formularioValido) return

        viewModelScope.launch {
            _crearUiState.update { it.copy(enviando = true, errorGeneral = null) }
            delay(1500)

            val prioridadObj = when(estadoActual.prioridad) {
                "Alta" -> Prioridad.Alta
                "Media" -> Prioridad.Media
                else -> Prioridad.Baja
            }

            val nuevoRecordatorio = Recordatorio(
                id = System.currentTimeMillis(),
                titulo = estadoActual.titulo,
                descripcion = estadoActual.descripcion.ifEmpty { null },
                categoria = estadoActual.categoria.ifEmpty { "General" },
                prioridad = prioridadObj,
                estado = EstadoRecordatorio.Pendiente
            )

            try {
                repository.guardar(nuevoRecordatorio)
                _crearUiState.update { it.copy(enviando = false, guardado = true) }
            } catch (e: Exception) {
                _crearUiState.update { it.copy(enviando = false, errorGeneral = "Ocurrió un error al guardar") }
            }
        }
    }
}