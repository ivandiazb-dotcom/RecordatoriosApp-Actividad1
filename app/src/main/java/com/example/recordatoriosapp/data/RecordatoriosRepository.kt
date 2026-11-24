package com.example.recordatoriosapp.data

import com.example.recordatoriosapp.model.EstadoRecordatorio
import com.example.recordatoriosapp.model.Prioridad
import com.example.recordatoriosapp.model.Recordatorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

sealed interface SyncResult {
    data object Success : SyncResult
    data object Offline : SyncResult
    data class RetryableError(val message: String) : SyncResult
    data class PermanentError(val message: String) : SyncResult
}

// Contrato del repositorio
interface RecordatoriosRepository {
    fun observarRecordatorios(): Flow<List<Recordatorio>>
    fun observarPorId(id: Long): Flow<Recordatorio?>
    fun observarPorEstado(completado: Boolean): Flow<List<Recordatorio>>
    suspend fun obtenerPorId(id: Long): Recordatorio?
    suspend fun guardar(recordatorio: Recordatorio)
    suspend fun actualizarCompletado(id: Long, completado: Boolean)
    suspend fun eliminar(recordatorio: Recordatorio)
    suspend fun sincronizar(): SyncResult
}

// Implementación Local-First: Room es la fuente de verdad y Retrofit sincroniza sin duplicados
class RoomRecordatoriosRepository(
    private val reminderDao: ReminderDao,
    private val apiService: ReminderApiService
) : RecordatoriosRepository {

    private val syncMutex = Mutex()

    override fun observarRecordatorios(): Flow<List<Recordatorio>> =
        reminderDao.observeAll().map { lista -> lista.map { it.toDomain() } }

    override fun observarPorId(id: Long): Flow<Recordatorio?> =
        reminderDao.observeById(id.toString()).map { it?.toDomain() }

    override fun observarPorEstado(completado: Boolean): Flow<List<Recordatorio>> =
        reminderDao.observeByStatus(completado).map { lista -> lista.map { it.toDomain() } }

    override suspend fun obtenerPorId(id: Long): Recordatorio? =
        reminderDao.findById(id.toString())?.toDomain()

    // 1. Guarda primero en Room como PENDING (funciona offline al instante)
    override suspend fun guardar(recordatorio: Recordatorio) {
        val entidad = recordatorio.toEntity().copy(
            updatedAt = System.currentTimeMillis(),
            syncStatus = SyncStatus.PENDING
        )
        reminderDao.save(entidad)
    }

    // 2. Actualiza en Room y marca como PENDING para sincronizar el cambio
    override suspend fun actualizarCompletado(id: Long, completado: Boolean) {
        val actual = reminderDao.findById(id.toString()) ?: return
        reminderDao.save(
            actual.copy(
                completed = completado,
                updatedAt = System.currentTimeMillis(),
                syncStatus = SyncStatus.PENDING
            )
        )
    }

    override suspend fun eliminar(recordatorio: Recordatorio) {
        reminderDao.delete(recordatorio.toEntity())
        // Intento controlado de eliminación remota sin bloquear la UI
        safeApiCall { apiService.delete(recordatorio.id.toString()) }
    }

    // 3. Sincronización controlada con Mutex (evita ejecuciones simultáneas y duplicados)
    override suspend fun sincronizar(): SyncResult = syncMutex.withLock {
        // Paso A: Subir cambios locales pendientes (PENDING -> PUT -> SYNCED)
        val pendientes = reminderDao.observeAll().first()
            .filter { it.syncStatus == SyncStatus.PENDING }

        for (entidad in pendientes) {
            when (val resultado = safeApiCall { apiService.upsert(entidad.id, entidad.toDto()) }) {
                is RemoteResult.Success -> {
                    reminderDao.save(
                        entidad.copy(
                            remoteId = resultado.data.id,
                            syncStatus = SyncStatus.SYNCED
                        )
                    )
                }
                is RemoteResult.NoConnection -> return@withLock SyncResult.Offline
                is RemoteResult.Timeout -> return@withLock SyncResult.RetryableError("Tiempo de espera agotado (Timeout). Tus datos locales siguen seguros.")
                is RemoteResult.HttpError -> {
                    if (resultado.code in 500..599) {
                        return@withLock SyncResult.RetryableError("Error del servidor (${resultado.code}). Se reintentará más tarde.")
                    } else {
                        reminderDao.save(entidad.copy(syncStatus = SyncStatus.ERROR))
                        return@withLock SyncResult.PermanentError("Solicitud inválida (${resultado.code}).")
                    }
                }
                is RemoteResult.InvalidData -> {
                    return@withLock SyncResult.PermanentError("Respuesta inválida del servidor.")
                }
            }
        }

        // Paso B: Descargar cambios remotos (GET -> toEntity -> @Upsert en Room)
        when (val remoto = safeApiCall { apiService.getAll() }) {
            is RemoteResult.Success -> {
                remoto.data.forEach { dto ->
                    val local = reminderDao.findById(dto.id)
                    // No sobrescribir si el usuario tiene un cambio local pendiente
                    if (local == null || local.syncStatus != SyncStatus.PENDING) {
                        reminderDao.save(dto.toEntity())
                    }
                }
                SyncResult.Success
            }
            is RemoteResult.NoConnection -> SyncResult.Offline
            is RemoteResult.Timeout -> SyncResult.RetryableError("Tiempo de espera agotado al consultar el servidor.")
            is RemoteResult.HttpError -> {
                if (remoto.code in 500..599) {
                    SyncResult.RetryableError("Servidor no disponible (${remoto.code}). Mostrando datos locales.")
                } else {
                    SyncResult.PermanentError("Error HTTP ${remoto.code} al sincronizar.")
                }
            }
            is RemoteResult.InvalidData -> SyncResult.PermanentError("Datos remotos inválidos.")
        }
    }
}

// --- Mapeadores explícitos entre Room (Entity) y Dominio (Recordatorio) ---

fun ReminderEntity.toDomain(): Recordatorio = Recordatorio(
    id = id.toLongOrNull() ?: id.hashCode().toLong(),
    titulo = title,
    descripcion = description,
    categoria = category,
    prioridad = when (priority) {
        ReminderPriority.HIGH -> Prioridad.Alta
        ReminderPriority.MEDIUM -> Prioridad.Media
        ReminderPriority.LOW -> Prioridad.Baja
    },
    estado = if (completed) EstadoRecordatorio.Completado else EstadoRecordatorio.Pendiente
)

fun Recordatorio.toEntity(): ReminderEntity = ReminderEntity(
    id = id.toString(),
    title = titulo,
    description = descripcion,
    category = categoria,
    priority = when (prioridad) {
        Prioridad.Alta -> ReminderPriority.HIGH
        Prioridad.Media -> ReminderPriority.MEDIUM
        Prioridad.Baja -> ReminderPriority.LOW
    },
    completed = estado is EstadoRecordatorio.Completado
)