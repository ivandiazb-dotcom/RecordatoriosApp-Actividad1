package com.example.recordatoriosapp.data

import com.squareup.moshi.Json

data class ReminderDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val category: String = "General",
    val priority: String = "MEDIUM",
    val completed: Boolean = false,
    @Json(name = "due_at") val dueAt: Long? = null,
    @Json(name = "updated_at") val updatedAt: Long = 0L
)

// Mapeo explícito: DTO remoto -> Entidad de Room (nunca llega a la UI)
fun ReminderDto.toEntity(): ReminderEntity = ReminderEntity(
    id = id,
    remoteId = id,
    title = title,
    description = description,
    category = category,
    priority = runCatching { ReminderPriority.valueOf(priority.uppercase()) }
        .getOrDefault(ReminderPriority.MEDIUM),
    completed = completed,
    dueAt = dueAt,
    updatedAt = updatedAt,
    syncStatus = SyncStatus.SYNCED
)

// Mapeo explícito: Entidad de Room -> DTO remoto para enviar con PUT
fun ReminderEntity.toDto(): ReminderDto = ReminderDto(
    id = id,
    title = title,
    description = description,
    category = category,
    priority = priority.name,
    completed = completed,
    dueAt = dueAt,
    updatedAt = updatedAt
)