package com.example.recordatoriosapp.navigation

import kotlinx.serialization.Serializable

@Serializable
data object ListaRoute

@Serializable
data object CrearRoute

// Al detalle solo le pasamos el ID, no el objeto completo
@Serializable
data class DetalleRoute(val recordatorioId: Long)