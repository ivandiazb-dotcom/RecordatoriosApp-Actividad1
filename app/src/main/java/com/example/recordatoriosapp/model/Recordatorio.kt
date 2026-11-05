package com.example.recordatoriosapp.model

// Jerarquía sellada para estados finitos del recordatorio
sealed class EstadoRecordatorio {
    data object Pendiente : EstadoRecordatorio()
    data object EnProgreso : EstadoRecordatorio()
    data object Completado : EstadoRecordatorio()
}

// Jerarquía sellada para los niveles de prioridad
sealed class Prioridad {
    data object Alta : Prioridad()
    data object Media : Prioridad()
    data object Baja : Prioridad()
}

// Data class inmutable con manejo de nulos (Null Safety) en la descripción
data class Recordatorio(
    val id: Long,
    val titulo: String,
    val descripcion: String?, // El símbolo '?' indica que el dato puede ser nulo
    val categoria: String,
    val prioridad: Prioridad,
    val estado: EstadoRecordatorio
)