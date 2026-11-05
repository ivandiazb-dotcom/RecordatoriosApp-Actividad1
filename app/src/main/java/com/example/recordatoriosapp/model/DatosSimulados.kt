package com.example.recordatoriosapp.model
import androidx.compose.ui.graphics.Color
object DatosSimulados {
    // Uso de Set para almacenar categorías sin duplicados
    val categoriasUnicas: Set<String> = setOf(
        "Trabajo", "Personal", "Estudio", "Trabajo" // El duplicado se ignora
    )

    // Uso de Map para asociar categorías con colores visuales
    val coloresCategoria: Map<String, Color> = mapOf(
        "Trabajo" to Color(0xFF1976D2),
        "Personal" to Color(0xFF388E3C),
        "Estudio" to Color(0xFFF57C00)
    )

    // Uso de List para inyectar datos de prueba en la interfaz
    val listaRecordatorios: List<Recordatorio> = listOf(
        Recordatorio(
            id = 1L,
            titulo = "Entregar Actividad 1",
            descripcion = "Subir el PDF con las capturas a la plataforma",
            categoria = "Estudio",
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.Pendiente
        ),
        Recordatorio(
            id = 2L,
            titulo = "Comprar víveres",
            descripcion = null, // Dato opcional ausente manejado correctamente
            categoria = "Personal",
            prioridad = Prioridad.Media,
            estado = EstadoRecordatorio.Completado
        ),
        Recordatorio(
            id = 3L,
            titulo = "Reunión de avance",
            descripcion = "Revisar arquitectura del proyecto",
            categoria = "Trabajo",
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.EnProgreso
        )
    )
}