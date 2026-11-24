package com.example.recordatoriosapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recordatoriosapp.data.NetworkMonitor
import com.example.recordatoriosapp.data.RecordatoriosRepository
import com.example.recordatoriosapp.data.SyncResult
import com.example.recordatoriosapp.model.EstadoRecordatorio
import com.example.recordatoriosapp.model.Prioridad
import com.example.recordatoriosapp.model.Recordatorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecordatoriosViewModel(
    private val repository: RecordatoriosRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _listaUiState = MutableStateFlow<ListaRecordatoriosUiState>(ListaRecordatoriosUiState.Cargando)
    val listaUiState: StateFlow<ListaRecordatoriosUiState> = _listaUiState.asStateFlow()

    private val _crearUiState = MutableStateFlow(CrearRecordatorioUiState())
    val crearUiState: StateFlow<CrearRecordatorioUiState> = _crearUiState.asStateFlow()

    private val _estadoRed = MutableStateFlow<String?>(null)
    val estadoRed: StateFlow<String?> = _estadoRed.asStateFlow()

    init {
        cargarRecordatorios()
        observarConectividad()
    }

    fun cargarRecordatorios() {
        viewModelScope.launch {
            repository.observarRecordatorios().collect { lista ->
                if (lista.isEmpty()) {
                    _listaUiState.value = ListaRecordatoriosUiState.Vacia
                } else {
                    _listaUiState.value = ListaRecordatoriosUiState.ConDatos(lista)
                }
            }
        }
    }

    private fun observarConectividad() {
        viewModelScope.launch {
            networkMonitor.observeConnectivity().collect { conectado ->
                if (conectado) {
                    sincronizarAhora()
                } else {
                    _estadoRed.value = "Sin conexión. Los cambios se guardan localmente."
                }
            }
        }
    }

    fun sincronizarAhora() {
        viewModelScope.launch {
            when (val resultado = repository.sincronizar()) {
                is SyncResult.Success -> _estadoRed.value = null
                is SyncResult.Offline -> _estadoRed.value = "Sin conexión. Mostrando datos locales."
                is SyncResult.RetryableError -> _estadoRed.value = resultado.message
                is SyncResult.PermanentError -> _estadoRed.value = resultado.message
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

            val prioridadObj = when (estadoActual.prioridad) {
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
                // 1. Guarda primero en Room como PENDING (funciona offline)
                repository.guardar(nuevoRecordatorio)
                _crearUiState.update { it.copy(enviando = false, guardado = true) }
                // 2. Intenta sincronizar en segundo plano sin bloquear la UI
                sincronizarAhora()
            } catch (e: Exception) {
                _crearUiState.update { it.copy(enviando = false, errorGeneral = "Ocurrió un error al guardar") }
            }
        }
    }

    fun alternarCompletado(recordatorio: Recordatorio) {
        viewModelScope.launch {
            val nuevoEstadoCompletado = recordatorio.estado !is EstadoRecordatorio.Completado
            repository.actualizarCompletado(recordatorio.id, nuevoEstadoCompletado)
            sincronizarAhora()
        }
    }

    fun eliminarRecordatorio(recordatorio: Recordatorio) {
        viewModelScope.launch {
            repository.eliminar(recordatorio)
        }
    }

    companion object {
        fun provideFactory(
            repository: RecordatoriosRepository,
            networkMonitor: NetworkMonitor
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecordatoriosViewModel(repository, networkMonitor) as T
            }
        }
    }
}