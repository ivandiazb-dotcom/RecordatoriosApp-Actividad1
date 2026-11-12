package com.example.recordatoriosapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.recordatoriosapp.ui.screens.CrearRecordatorioScreen
import com.example.recordatoriosapp.ui.screens.DetalleRecordatorioScreen
import com.example.recordatoriosapp.ui.screens.ListaRecordatoriosScreen
import com.example.recordatoriosapp.viewmodel.ListaRecordatoriosUiState
import com.example.recordatoriosapp.viewmodel.RecordatoriosViewModel

@Composable
fun RecordatoriosNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: RecordatoriosViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = ListaRoute) {

        composable<ListaRoute> {
            val uiState by viewModel.listaUiState.collectAsStateWithLifecycle()
            ListaRecordatoriosScreen(
                uiState = uiState,
                onCrearClick = { navController.navigate(CrearRoute) },
                onRecordatorioClick = { id -> navController.navigate(DetalleRoute(id)) }
            )
        }

        composable<CrearRoute> {
            val uiState by viewModel.crearUiState.collectAsStateWithLifecycle()

            if (uiState.guardado) {
                viewModel.resetearFormulario()
                viewModel.cargarRecordatorios()
                navController.popBackStack()
            }

            CrearRecordatorioScreen(
                uiState = uiState,
                onTituloChange = { viewModel.actualizarTitulo(it) },
                onDescripcionChange = { viewModel.actualizarDescripcion(it) },
                onGuardarClick = { viewModel.guardarRecordatorio() },
                viewModel = viewModel // ¡Esta es la línea que solucionará el error!
            )
        }

        composable<DetalleRoute> { backStackEntry ->
            val ruta = backStackEntry.toRoute<DetalleRoute>()
            val uiState by viewModel.listaUiState.collectAsStateWithLifecycle()

            val recordatorio = if (uiState is ListaRecordatoriosUiState.ConDatos) {
                (uiState as ListaRecordatoriosUiState.ConDatos).recordatorios.find { it.id == ruta.recordatorioId }
            } else null

            DetalleRecordatorioScreen(
                recordatorio = recordatorio,
                onVolverClick = { navController.popBackStack() }
            )
        }
    }
}