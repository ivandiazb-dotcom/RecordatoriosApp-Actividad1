package com.example.recordatoriosapp.data

import com.example.recordatoriosapp.model.Recordatorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// La interfaz define qué se puede hacer, no cómo se hace
interface RecordatoriosRepository {
    fun observarRecordatorios(): Flow<List<Recordatorio>>
    suspend fun obtenerPorId(id: Long): Recordatorio?
    suspend fun guardar(recordatorio: Recordatorio)
}

// La implementación guarda los datos temporalmente en memoria
class RecordatoriosEnMemoriaRepository : RecordatoriosRepository {

    // Iniciamos con una lista vacía para poder probar el Estado Vacío
    private val recordatorios = MutableStateFlow<List<Recordatorio>>(emptyList())

    override fun observarRecordatorios(): Flow<List<Recordatorio>> = recordatorios

    override suspend fun obtenerPorId(id: Long): Recordatorio? {
        return recordatorios.value.firstOrNull { it.id == id }
    }

    override suspend fun guardar(recordatorio: Recordatorio) {
        recordatorios.update { actuales ->
            actuales + recordatorio
        }
    }
}